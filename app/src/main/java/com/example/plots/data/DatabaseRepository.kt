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

    fun getCurrentUserListingItems(
        getCurrentUserListingItemsSucceeded: (array: ArrayList<PropertyCardView>) -> Unit,
        getCurrentUserListingItemsFailed: () -> Unit) =
        dataSrc.getCurrentUserListingItems(getCurrentUserListingItemsSucceeded,
            getCurrentUserListingItemsFailed)

    fun getAllPhotosOfProperty(propertyId: String,
                               getAllPhotosOfPropertySucceeded: (bitmaps: ArrayList<Bitmap>) -> Unit,
                               getAllPhotosOfPropertyFailed: () -> Unit) =
        dataSrc.getAllPhotosOfProperty(propertyId, getAllPhotosOfPropertySucceeded,
            getAllPhotosOfPropertyFailed)

    fun getPropertyInformationById(propertyId: String,
                                   getPropertyInformationByIdSucceeded: (property: Property?) -> Unit,
                                   getPropertyInformationByIdFailed: () -> Unit) =
        dataSrc.getPropertyInformationById(propertyId, getPropertyInformationByIdSucceeded,
            getPropertyInformationByIdFailed)

    fun getPropertyOwnersInformationById(propertyId: String,
                                         getPropertyOwnersInformationByIdSucceeded: (ownerData: OwnerData?) -> Unit,
                                         getPropertyOwnersInformationByIdFailed: () -> Unit) =
        dataSrc.getPropertyOwnersInformationById(propertyId,
            getPropertyOwnersInformationByIdSucceeded, getPropertyOwnersInformationByIdFailed)

    fun deletePropertyById(propertyId: String,
                           deletePropertyByIdSucceeded: () -> Unit,
                           deletePropertyByIdFailed: () -> Unit) =
        dataSrc.deletePropertyById(propertyId, deletePropertyByIdSucceeded, deletePropertyByIdFailed)

    fun getAllListingItems(propertyType: String, listingType: String,
        getAllListingItemsSucceeded: (array: ArrayList<PropertyCardView>) -> Unit,
        getAllListingItemsFailed: () -> Unit) =
        dataSrc.getAllListingItems(propertyType, listingType,
            getAllListingItemsSucceeded, getAllListingItemsFailed)

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