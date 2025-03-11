package com.example.myapplication

const val SHADER_SRC = """
    uniform float2 size;
    uniform float time;
    uniform shader myShader;

    half4 main(float2 fragCoord){
        float scale = 1 / size.x;
        float2 scaledCoord = fragCoord * scale;
        float2 center = size * 0.5 * scale;
        float dist = distance(scaledCoord, center);
        float2 dir = scaledCoord - center;
        float sin = sin(dist * 70 - time * 6.28);
        float2 offset = dir * sin;
        float2 imageCoord = scaledCoord + offset / 30;
        return myShader.eval(imageCoord / scale);
    } 
"""
 const val SHADER_SRC2 = """
    uniform float2 size;
    uniform float time;
    uniform shader myShader;

    half4 main(float2 fragCoord) {
        // Asosiy gradientni olish
        half4 baseColor = myShader.eval(fragCoord);
        
        // Yorug‘lik effekti
        float glowIntensity = 0.2 + 0.05 * sin(time * 2.0); // Yorug‘lik pulsatsiyasi
        float2 center = size * 0.5;
        float dist = distance(fragCoord, center) / (size.x * 0.5);
        
        // Yumshoq nurlanish (gradient buzilmaydi)
        float glow = exp(-dist * 4.0) * glowIntensity;
        
        // Yorug‘lik rangi (oq-yashil jilosi)
        half3 glowColor = half3(0.9, 1.0, 0.8) * glow;
        
        // Asosiy gradient bilan aralashtirish
        half3 finalColor = baseColor.rgb + glowColor;
        
        return half4(finalColor, baseColor.a);
    }
"""



const val shader = """
    uniform float2 size;
uniform float time;
uniform shader myShader;

// Rotation matrix
float2x2 rot(float a) {
    float c = cos(a), s = sin(a);
    return float2x2(c, s, -s, c);
}

// Box SDF function
float sdBox(float3 p, float3 b) {
    float3 q = abs(p) - b;
    return length(max(q, 0.0)) + min(max(q.x, max(q.y, q.z)), 0.0);
}

float box(float3 pos, float scale) {
    pos *= scale;
    float base = sdBox(pos, float3(0.4, 0.4, 0.1)) / 1.5;
    pos.xy *= 5.0;
    pos.y -= 3.5;
    pos.xy = rot(0.75) * pos.xy;
    return -base;
}

float box_set(float3 pos, float time) {
    float3 orig = pos;
    
    pos.y += sin(time * 0.4) * 2.5;
    pos.xy = rot(0.8) * pos.xy;
    float b1 = box(pos, 2.0 - abs(sin(time * 0.4)) * 1.5);

    pos = orig;
    pos.y -= sin(time * 0.4) * 2.5;
    pos.xy = rot(0.8) * pos.xy;
    float b2 = box(pos, 2.0 - abs(sin(time * 0.4)) * 1.5);

    pos = orig;
    pos.x += sin(time * 0.4) * 2.5;
    pos.xy = rot(0.8) * pos.xy;
    float b3 = box(pos, 2.0 - abs(sin(time * 0.4)) * 1.5);

    pos = orig;
    pos.x -= sin(time * 0.4) * 2.5;
    pos.xy = rot(0.8) * pos.xy;
    float b4 = box(pos, 2.0 - abs(sin(time * 0.4)) * 1.5);

    pos = orig;
    pos.xy = rot(0.8) * pos.xy;
    float b5 = box(pos, 0.5) * 6.0;

    pos = orig;
    float b6 = box(pos, 0.5) * 6.0;

    return max(max(max(max(max(b1, b2), b3), b4), b5), b6);
}

float map(float3 pos, float time) {
    return box_set(pos, time);
}

half4 main(float2 fragCoord) {
    // Orqa fon rangini olish
    half4 baseColor = myShader.eval(fragCoord);
    

    float2 p = (fragCoord * 8.0 - size) / min(size.x, size.y);
    float3 ro = float3(0.0, -0.2, time * 4.0);
    float3 ray = normalize(float3(p, 1.5));
    ray.xy = rot(sin(time * 0.03) * 5.0) * ray.xy;
    ray.yz = rot(sin(time * 0.05) * 0.2) * ray.yz;

    float t = 0.1;
    float3 col = float3(0.0);
    float ac = 0.0;

    for (int i = 0; i < 99; i++) {
        float3 pos = ro + ray * t;
        pos = mod(pos - 2.0, 4.0) - 2.0;  // <-- AGSL-da `mod` ishlatildi

        float d = map(pos, time);
        d = max(abs(d), 0.01);
        ac += exp(-d * 23.0);
        t += d * 0.55;
    }

    // Gradient ranglarini moslashtirish
    float3 gradientStartColor = float3(0.01, 0.62, 0.24); // #019F3C
    float3 gradientEndColor = float3(1.0, 1.0, 1.0); // #FFFFFF
    float3 effectColor = mix(gradientStartColor, gradientEndColor, abs(sin(time * 0.5)));

    // Effektlarni vertikal joylashuvga bog'lash
    float verticalFactor = smoothstep(0.0, 0.5, fragCoord.y / size.y); // Tepa qismda effektlarni kuchaytirish
    ac *= verticalFactor; // Pastki qismda effektlarni kamaytirish

    // Orqa fonni ko'rinishi uchun baseColor ni qo'shish
    float3 finalColor = baseColor.rgb + ac * 0.02 * effectColor;

    // Orqa fonni shaffof qilish (agar kerak bo'lsa)
    float alpha = baseColor.a; // Orqa fonning shaffofligi
    return half4(finalColor, alpha);
}
    """