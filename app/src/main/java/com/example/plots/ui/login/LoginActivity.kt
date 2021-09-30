package com.example.plots.ui.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.plots.R
import com.example.plots.activities.MainActivity
import com.example.plots.ui.register.RegisterActivity
import com.example.plots.databinding.ActivityLoginBinding
import com.example.plots.dialogs.ErrorDialog
import com.example.plots.dialogs.LoadingScreen
import com.example.plots.utils.AuthenticationInjector
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"

    private lateinit var binding: ActivityLoginBinding

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInActivityLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: started")
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        googleSignInActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val viewModel: LoginViewModel by viewModels {
                        AuthenticationInjector.provideLoginViewModelFactory() }
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val loadingScreen = LoadingScreen(this,
                            R.layout.google_login_loading_screen).also { it.start() }

                        val account = task.getResult(ApiException::class.java)
                        Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                        viewModel.signInWithGoogle(account.idToken!!, {
                            Log.d(TAG, "firebaseAuthWithGoogle: succeeded")
                            loadingScreen.end()
                            finish()
                            startActivity(Intent(this, MainActivity::class.java))
                        }, {
                            Log.w(TAG, "firebaseAuthWithGoogle: failed")
                            loadingScreen.end()
                            val errorDialog = ErrorDialog(this, R.layout.google_login_error)
                            errorDialog.start()
                        })
                    } catch(e: ApiException) {
                        Log.w(TAG, "Google sign in failed", e)
                    }
                }
            }

        initUI()
        Log.d(TAG, "onCreate: ended")
    }

    private fun initUI() {
        val factory = AuthenticationInjector.provideLoginViewModelFactory()
        val viewModel: LoginViewModel by viewModels { factory }

        viewModel.getGoogleSignInClient(this, getString(R.string.default_web_client_id_2))
            .observe(this, {
                googleSignInClient = it
            })

        binding.login.setOnClickListener {
            if(checkLoginDataForSignIn()) {
                val loadingScreen = LoadingScreen(this, R.layout.login_loading_screen)
                loadingScreen.start()

                viewModel.signInWithEmail(
                    binding.email.text.toString(),
                    binding.password.text.toString(), {
                        Log.d(TAG, "Sign In with email: succeeded")
                        loadingScreen.end()
                        finish()
                        startActivity(Intent(this, MainActivity::class.java))
                    }, {
                        Log.d(TAG, "Sign In with email: failed")
                        loadingScreen.end()
                        val errorDialog = ErrorDialog(this, R.layout.login_error)
                        errorDialog.start()
                    }
                )
            }
        }

        binding.signUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.googleSignIn.setOnClickListener {
            Log.d(TAG,"googleSignIn: started")
            val loadingScreen = LoadingScreen(this, R.layout.google_login_loading_screen)
                .also { it.start() }
            googleSignInActivityLauncher.launch(googleSignInClient.signInIntent)
            Log.d(TAG,"googleSignIn: ended")
        }

    }

    private fun checkLoginDataForSignIn(): Boolean {
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
        return true
    }

}