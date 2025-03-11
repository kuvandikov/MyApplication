package com.example.myapplication

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivityMain2Binding
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private var myShader: RuntimeShader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            lifecycleScope.launch {
                val startTime = System.nanoTime()
                while (true) {
                    val timeM = (System.currentTimeMillis() % 1_000L) / 1_000f
                    val currentTime = (System.nanoTime() - startTime) / 1_000_000_000f
                    myShader = RuntimeShader(SHADER_SRC2)
                    myShader?.let {
                        val width = binding.root.width.toFloat()
                        val height = binding.root.height.toFloat()
                        myShader?.setFloatUniform("time", currentTime)
                        myShader?.setFloatUniform("size",width,height)
                        binding.root.setRenderEffect(RenderEffect.createRuntimeShaderEffect(it,"myShader"))
                        Log.d("TAG", "ImageView size changed: $width x $height")
                    }
                    Log.d("TAG", "lifecycleScope: $timeM")
                    delay(100)
                }
            }
        }
    }
}