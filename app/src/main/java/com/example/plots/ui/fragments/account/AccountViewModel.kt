package com.example.plots.ui.fragments.account

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.plots.data.DatabaseRepository

class AccountViewModel(private val repo: DatabaseRepository) : ViewModel() {

    fun getUserEmail(): LiveData<String> = repo.getUserEmail()
    fun getUserPhone(): LiveData<String> = repo.getUserPhone()
    fun getUserName(): LiveData<String> = repo.getUserName()
    fun signOutUser() = repo.signOutUser()

    fun getProfileImage(getGoogleProfileImageSucceeded: (imageUri: Uri) -> Unit,
                        getEmailProfileImageSucceeded: (bitmap: Bitmap?) -> Unit,
                        getProfileImageFailure: () -> Unit) =
        repo.getProfileImage(getGoogleProfileImageSucceeded,
            getEmailProfileImageSucceeded, getProfileImageFailure)
}