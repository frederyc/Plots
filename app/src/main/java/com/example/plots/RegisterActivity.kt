package com.example.plots

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.plots.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signIn.setOnClickListener {
            finish()
        }
    }
}