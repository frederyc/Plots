package com.example.plots.data

import androidx.lifecycle.LiveData

class AuthenticationRepository private constructor(
    private val dataSrc: RemoteDataSourceFirebase) {

    fun signInWithEmail(email: String, password: String) {
        dataSrc.signInWithEmail(email, password)
    }

    fun createUserWithEmail(name: String, phone: String, email: String, password: String) {
        dataSrc.createUserWithEmail(name, phone, email, password)
    }

    fun getSignInUserWithEmailResult(): LiveData<Boolean> = dataSrc.getSignInUserWithEmailResult()

    fun getCreateUserWithEmailResult(): LiveData<Boolean> = dataSrc.getCreateUserWithEmailResult()

    fun getCreateUserPersonalDataResult(): LiveData<Boolean> =
        dataSrc.getCreateUserPersonalDataResult()

    companion object {
        @Volatile private var instance: AuthenticationRepository? = null

        fun getInstance(dataSrc: RemoteDataSourceFirebase) =
            instance ?: synchronized(this) {
                instance ?: AuthenticationRepository(dataSrc).also {
                    instance = it
                }
            }
    }

}