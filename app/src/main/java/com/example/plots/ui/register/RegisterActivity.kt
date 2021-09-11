package com.example.plots.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import com.example.plots.R
import com.example.plots.databinding.ActivityRegisterBinding
import com.example.plots.dialogs.ErrorDialog
import com.example.plots.dialogs.LoadingScreen
import com.example.plots.utils.AuthenticationInjector
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private val TAG = "RegisterActivity"

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initUI()
    }

    private fun initUI() {
        val factory = AuthenticationInjector.provideRegisterViewModelFactory()
        val viewModel: RegisterViewModel by viewModels { factory }

        binding.signIn.setOnClickListener {
            finish()
        }

        binding.signUp.setOnClickListener {
            if(checkUserDataForUpload()) {
                val loadingScreen = LoadingScreen(this, R.layout.register_loading_screen)
                    .also { it.start() }

                viewModel.createUserWithEmail(
                    binding.name.text.toString(),
                    binding.phone.text.toString(),
                    binding.email.text.toString(),
                    binding.password.text.toString())

                viewModel.getCreateUserWithEmailResult().observe(this, {
                    if(it) {
                        viewModel.getCreateUserPersonalDataResult().observe(this, { p ->
                            if(p) {
                                Log.d(TAG, "User registered successfully")
                                Toast.makeText(baseContext,
                                    "Registration successful!", Toast.LENGTH_LONG).show()
                                loadingScreen.end()
                                finish()
                            }
                            else {
                                Log.w(TAG, "User failed to register (personal data")
                                loadingScreen.end()
                                ErrorDialog(this, R.layout.register_error).start()
                            }
                        })
                    }
                    else {
                        Log.w(TAG, "User failed to register")
                        Toast.makeText(baseContext,
                            "User failed to register", Toast.LENGTH_SHORT).show()
                        loadingScreen.end()
                    }
                })

            }
        }

    }

    private fun checkUserDataForUpload(): Boolean {
        if(binding.name.text.toString().isEmpty()) {
            binding.name.error = "Please enter your name"
            binding.name.requestFocus()
            return false
        }
        if(binding.phone.text.toString().isEmpty()) {
            binding.phone.error = "Please enter your phone number"
            binding.phone.requestFocus()
            return false
        }
        if(binding.email.text.toString().isEmpty()) {
            binding.email.error = "Please enter email"
            binding.email.requestFocus()
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()) {
            binding.email.error = "Please enter a valid email address"
            binding.email.requestFocus()
            return false
        }
        if(binding.password.text.toString().isEmpty()) {
            binding.password.error = "Please enter a password"
            binding.password.requestFocus()
            return false
        }
        if(binding.confirmPassword.text.toString().isEmpty()) {
            binding.confirmPassword.error = "Please confirm your password"
            binding.confirmPassword.requestFocus()
            return false
        }
        if(binding.confirmPassword.text.toString() != binding.password.text.toString()) {
            binding.confirmPassword.error = "Passwords must match"
            binding.confirmPassword.requestFocus()
            return false
        }
        return true
    }

}