package com.example.plots.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.plots.R
import com.example.plots.ui.register.RegisterActivity
import com.example.plots.databinding.ActivityLoginBinding
import com.example.plots.dialogs.LoadingScreen
import com.example.plots.utils.AuthenticationInjector


class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        val factory = AuthenticationInjector.provideLoginViewModelFactory()
        val viewModel: LoginViewModel by viewModels { factory }

        binding.login.setOnClickListener {
            val loadingScreen = LoadingScreen(this, R.layout.login_loading_screen)
            loadingScreen.start()
            viewModel.signInWithEmail(binding.email.text.toString(),
                    binding.password.text.toString())
            viewModel.getSignInUserWithEmailResult().observe(this, {
                if(it) {
                    Log.d(TAG, "User signed in successfully")
                    loadingScreen.end()
                    finish()
                }
                else {
                    Log.w(TAG, "User failed to login")
                    Toast.makeText(baseContext, "User failed to login", Toast.LENGTH_SHORT).show()
                    loadingScreen.end()
                }
            })
        }

        binding.signUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

}