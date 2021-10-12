package com.example.plots.ui.activities.createListing

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.plots.data.DatabaseRepository
import com.example.plots.data.Property

class CreateListingActivityViewModel(private val repo: DatabaseRepository) : ViewModel() {

    fun uploadPropertyToDatabase(
        property: Property,
        uris: ArrayList<Uri?>,
        uploadPropertyToDatabaseSucceeded: () -> Unit,
        uploadPropertyToDatabaseFailed: () -> Unit) = repo.uploadPropertyToDatabase(
        property, uris, uploadPropertyToDatabaseSucceeded, uploadPropertyToDatabaseFailed)
}