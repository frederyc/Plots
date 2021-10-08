package com.example.plots.ui.activities.updateCredentials

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.plots.data.DatabaseRepository

class AccountEditInformationViewModel(private val repo: DatabaseRepository): ViewModel() {

    fun getUserSignInType(getUserSignInTypeSucceeded: (signInType: String) -> Unit,
                          getUserSignInTypeFailure: () -> Unit)
        = repo.getUserSignInType(getUserSignInTypeSucceeded, getUserSignInTypeFailure)

    fun updateUserPhoneNumber(newPhoneNumber: String, updateUserPhoneNumberSucceeded: () -> Unit,
                              updateUserPhoneNumberFailure: () -> Unit) =
        repo.updateUserPhoneNumber(newPhoneNumber,
            updateUserPhoneNumberSucceeded, updateUserPhoneNumberFailure)

    fun updateUserName(newName: String, updateUserNameSucceeded: () -> Unit,
                       updateUserNameFailure: () -> Unit ) =
        repo.updateUserName(newName, updateUserNameSucceeded, updateUserNameFailure)

    fun uploadProfileImage(uri: Uri, uploadProfileImageSucceeded: () -> Unit,
                           uploadProfileImageFailed: () -> Unit) =
        repo.uploadProfileImage(uri, uploadProfileImageSucceeded, uploadProfileImageFailed)

    fun getProfileImage(getGoogleProfileImageSucceeded: (imageUri: Uri) -> Unit,
                        getEmailProfileImageSucceeded: (bitmap: Bitmap?) -> Unit,
                        getProfileImageFailure: () -> Unit) =
        repo.getProfileImage(getGoogleProfileImageSucceeded, getEmailProfileImageSucceeded,
            getProfileImageFailure)
}