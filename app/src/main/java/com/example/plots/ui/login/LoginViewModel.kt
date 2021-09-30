package com.example.plots.ui.login

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.plots.data.AuthenticationRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient

class LoginViewModel(private val repo: AuthenticationRepository) : ViewModel() {

    fun signInWithEmail(email: String, password: String, signInWithEmailSucceeded: () -> Unit,
                        signInWithEmailFailed: () -> Unit) {
        repo.signInWithEmail(email, password, signInWithEmailSucceeded, signInWithEmailFailed)
    }

    fun signInWithGoogle(idToken: String, signInWithGoogleSucceeded: () -> Unit,
                         signInWithGoogleFailed: () -> Unit) {
        repo.signInWithGoogle(idToken, signInWithGoogleSucceeded, signInWithGoogleFailed)
    }

    fun getGoogleSignInClient(activity: Activity, defaultWebClientID: String):
            LiveData<GoogleSignInClient> = repo.getGoogleSignInClient(activity, defaultWebClientID)

}