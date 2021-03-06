package com.example.plots.ui.register

import androidx.lifecycle.ViewModel
import com.example.plots.data.AuthenticationRepository

class RegisterViewModel(private val repo: AuthenticationRepository) : ViewModel() {

    fun createUserWithEmail(name: String, phone: String, email: String, password: String,
                            onAccountCreationSuccess: () -> Unit,
                            onAccountCreationFailure: () -> Unit) {
        repo.createUserWithEmail(name, phone, email, password,
            onAccountCreationSuccess, onAccountCreationFailure)
    }

}