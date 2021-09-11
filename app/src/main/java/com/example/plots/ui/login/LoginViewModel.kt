package com.example.plots.ui.login

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.plots.data.AuthenticationRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient

class LoginViewModel(private val repo: AuthenticationRepository) : ViewModel() {

    fun signInWithEmail(email: String, password: String) {
        repo.signInWithEmail(email, password)
    }

    fun signInWithGoogle(idToken: String) {
        repo.signInWithGoogle(idToken)
    }

    fun getSignInUserWithEmailResult(): LiveData<Boolean> = repo.getSignInUserWithEmailResult()

    fun getAuthUserWithGoogleResult(): LiveData<Boolean> = repo.getAuthUserWithGoogleResult()

    fun getGoogleSignInClient(activity: Activity, defaultWebClientID: String):
            LiveData<GoogleSignInClient> = repo.getGoogleSignInClient(activity, defaultWebClientID)

}