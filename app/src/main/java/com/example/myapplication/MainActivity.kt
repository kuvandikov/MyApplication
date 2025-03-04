package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.myapplication.databinding.ActivityMainBinding
import com.redmadrobot.inputmask.MaskedTextChangedListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonPhone.setOnClickListener { setPhoneMask() }
        binding.buttonCard.setOnClickListener { setCardMask() }
        binding.buttonNone.setOnClickListener { setNoMask() }

        setPhoneMask()
    }


    private fun setPhoneMask() {
        applyMask("+998 [00] [000]-[00]-[00]") // O'zbekiston telefon raqami formati
    }

    private fun setCardMask() {
        applyMask("[0000] [0000] [0000] [0000]") // Bank karta raqami formati
    }

    private fun setNoMask() {
        val hadFocus = binding.editText.hasFocus()
        // Listener ni to'liq o'chirish
        binding.editText.removeTextChangedListener(binding.editText.tag as? MaskedTextChangedListener)
        binding.editText.onFocusChangeListener = null
        binding.editText.tag = null

        // Oddiy EditText kabi ishlash uchun
        binding.editText.doAfterTextChanged {
            binding.textOutput.text = "Natija: ${it.toString()}"
        }


        binding.editText.setText("")
        if (hadFocus) {
            binding.editText.requestFocus()
        }

    }

    private fun applyMask(mask: String) {
        val hadFocus = binding.editText.hasFocus()
        // Eski listener ni o'chirish
        binding.editText.removeTextChangedListener(binding.editText.tag as? MaskedTextChangedListener)
        binding.editText.onFocusChangeListener = null

        // Yangi listener ni yaratish

        val listener = MaskedTextChangedListener(
            format = mask,
             field = binding.editText,
             valueListener = object : MaskedTextChangedListener.ValueListener {

                override fun onTextChanged(
                    maskFilled: Boolean,
                    extractedValue: String,
                    formattedValue: String,
                    tailPlaceholder: String
                ) {
                    // Foydalanuvchi kiritgan ma'lumotni chiqarish
                    binding.textOutput.text = "Natija: $extractedValue (Formatlangan: $formattedValue)"

                }
            }
        )

        // EditText ga listener ni biriktirish
        binding.editText.addTextChangedListener(listener)
        binding.editText.onFocusChangeListener = listener
        binding.editText.tag = listener

        // EditText ni tozalash
        binding.editText.setText("")

        if (hadFocus) {
            binding.editText.requestFocus()
        }
    }
}