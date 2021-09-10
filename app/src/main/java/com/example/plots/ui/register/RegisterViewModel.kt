package com.example.plots.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.plots.data.AuthenticationRepository

class RegisterViewModel(private val repo: AuthenticationRepository) : ViewModel() {

    fun createUserWithEmail(name: String, phone: String, email: String, password: String) {
        repo.createUserWithEmail(name, phone, email, password)
    }

    fun getCreateUserWithEmailResult(): LiveData<Boolean> = repo.getCreateUserWithEmailResult()

    fun getCreateUserPersonalDataResult(): LiveData<Boolean> =
        repo.getCreateUserPersonalDataResult()

}