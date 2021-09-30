package com.example.plots.ui.fragments.account

import android.app.Activity
import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.plots.data.AuthenticationRepository
import com.example.plots.data.DatabaseRepository

class AccountViewModel(private val repo: DatabaseRepository) : ViewModel() {

    fun getUserEmail(): LiveData<String> = repo.getUserEmail()
    fun getUserPhone(): LiveData<String> = repo.getUserPhone()
    fun getUserName(): LiveData<String> = repo.getUserName()
    fun signOutUser() = repo.signOutUser()
    fun getProfileImageUri(): LiveData<Uri>? = repo.getProfileImageUri()
}