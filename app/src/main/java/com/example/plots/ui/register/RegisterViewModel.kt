package com.example.plots.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.plots.data.AuthenticationRepository

class RegisterViewModel(private val repo: AuthenticationRepository) : ViewModel() {

    fun createUserWithEmail(email: String, password: String) {
        return repo.createUserWithEmail(email, password)
    }

    fun getCreateUserWithEmailResult(): LiveData<Boolean> = repo.getCreateUserWithEmailResult()

}