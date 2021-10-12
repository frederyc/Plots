package com.example.plots.data

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData

class DatabaseRepository private constructor(
    private val dataSrc: RemoteDataSourceFirebase) {

    fun getUserEmail(): LiveData<String> = dataSrc.getUserEmail()
    fun getUserPhone(): LiveData<String> = dataSrc.getUserPhone()
    fun getUserName(): LiveData<String> = dataSrc.getUserName()
    fun signOutUser() = dataSrc.signOutUser()

    fun getProfileImage(getGoogleProfileImageSucceeded: (imageUri: Uri) -> Unit,
                        getEmailProfileImageSucceeded: (bitmap: Bitmap?) -> Unit,
                        getProfileImageFailure: () -> Unit) =
        dataSrc.getProfileImage(getGoogleProfileImageSucceeded,
            getEmailProfileImageSucceeded, getProfileImageFailure)

    fun getUserSignInType(getUserSignInTypeSucceeded: (signInType: String) -> Unit,
                          getUserSignInTypeFailure: () -> Unit) =
        dataSrc.getUserSignInType(getUserSignInTypeSucceeded, getUserSignInTypeFailure)

    fun updateUserPhoneNumber(newPhoneNumber: String, updateUserPhoneNumberSucceeded: () -> Unit,
                              updateUserPhoneNumberFailure: () -> Unit) =
        dataSrc.updateUserPhoneNumber(newPhoneNumber,
            updateUserPhoneNumberSucceeded, updateUserPhoneNumberFailure)

    fun updateUserName(newName: String, updateUserNameSucceeded: () -> Unit,
                       updateUserNameFailure: () -> Unit ) =
        dataSrc.updateUserName(newName, updateUserNameSucceeded, updateUserNameFailure)

    fun uploadProfileImage(uri: Uri, uploadProfileImageSucceeded: () -> Unit,
                           uploadProfileImageFailed: () -> Unit) =
        dataSrc.uploadProfileImage(uri, uploadProfileImageSucceeded, uploadProfileImageFailed)

    fun uploadPropertyToDatabase(
        property: Property,
        uris: ArrayList<Uri?>,
        uploadPropertyToDatabaseSucceeded: () -> Unit,
        uploadPropertyToDatabaseFailed: () -> Unit) = dataSrc.uploadPropertyToDatabase(
        property, uris, uploadPropertyToDatabaseSucceeded, uploadPropertyToDatabaseFailed)

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