package com.example.plots.ui.activities.listingExpanded

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.plots.data.DatabaseRepository
import com.example.plots.data.OwnerData
import com.example.plots.data.Property

class ListingExpandedViewModel(private val repo: DatabaseRepository) :  ViewModel() {

    fun getAllPhotosOfProperty(propertyId: String,
                               getAllPhotosOfPropertySucceeded: (bitmaps: ArrayList<Bitmap>) -> Unit,
                               getAllPhotosOfPropertyFailed: () -> Unit) =
        repo.getAllPhotosOfProperty(propertyId, getAllPhotosOfPropertySucceeded,
            getAllPhotosOfPropertyFailed)

    fun getPropertyInformationById(propertyId: String,
                                   getPropertyInformationByIdSucceeded: (property: Property?) -> Unit,
                                   getPropertyInformationByIdFailed: () -> Unit) =
        repo.getPropertyInformationById(propertyId, getPropertyInformationByIdSucceeded,
            getPropertyInformationByIdFailed)

    fun getPropertyOwnersInformationById(propertyId: String,
                                         getPropertyOwnersInformationByIdSucceeded: (ownerData: OwnerData?) -> Unit,
                                         getPropertyOwnersInformationByIdFailed: () -> Unit) =
        repo.getPropertyOwnersInformationById(propertyId,
            getPropertyOwnersInformationByIdSucceeded, getPropertyOwnersInformationByIdFailed)

    fun getProfileImage(getGoogleProfileImageSucceeded: (imageUri: Uri) -> Unit,
                        getEmailProfileImageSucceeded: (bitmap: Bitmap?) -> Unit,
                        getProfileImageFailure: () -> Unit) =
        repo.getProfileImage(getGoogleProfileImageSucceeded, getEmailProfileImageSucceeded,
            getProfileImageFailure)

}