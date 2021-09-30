package com.example.plots.data

import android.net.Uri
import androidx.lifecycle.LiveData

class DatabaseRepository private constructor(
    private val dataSrc: RemoteDataSourceFirebase) {

    fun getUserEmail(): LiveData<String> = dataSrc.getUserEmail()
    fun getUserPhone(): LiveData<String> = dataSrc.getUserPhone()
    fun getUserName(): LiveData<String> = dataSrc.getUserName()
    fun signOutUser() = dataSrc.signOutUser()
    fun getProfileImageUri(): LiveData<Uri>? = dataSrc.getProfileImageUri()

    companion object {
        @Volatile private var instance: DatabaseRepository? = null

        fun getInstance(dataSrc: RemoteDataSourceFirebase) =
            instance ?: synchronized(this) {
                instance ?: DatabaseRepository(dataSrc).also {
                    instance = it
                }
            }
    }
}