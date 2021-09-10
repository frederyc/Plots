package com.example.plots.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.plots.data.AuthenticationRepository

class LoginViewModel(private val repo: AuthenticationRepository) : ViewModel() {

    fun signInWithEmail(email: String, password: String) {
        repo.signInWithEmail(email, password)
    }

    fun getSignInUserWithEmailResult(): LiveData<Boolean> = repo.getSignInUserWithEmailResult()

}