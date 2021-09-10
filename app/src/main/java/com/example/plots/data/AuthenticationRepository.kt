package com.example.plots.data

import androidx.lifecycle.LiveData

class AuthenticationRepository private constructor(
    private val dataSrc: RemoteDataSourceFirebase) {

    fun signInWithEmail(email: String, password: String) {
        dataSrc.signInWithEmail(email, password)
    }

    fun createUserWithEmail(email: String, password: String) {
        dataSrc.createUserWithEmail(email, password)
    }

    fun getSignInUserWithEmailResult(): LiveData<Boolean> = dataSrc.getSignInUserWithEmailResult()

    fun getCreateUserWithEmailResult(): LiveData<Boolean> = dataSrc.getCreateUserWithEmailResult()

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