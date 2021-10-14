package com.example.plots.ui.fragments.feed

import androidx.lifecycle.ViewModel
import com.example.plots.data.DatabaseRepository
import com.example.plots.data.PropertyCardView

class FeedFragmentViewModel(private val repo: DatabaseRepository) : ViewModel() {

    fun getAllListingItems(propertyType: String, listingType: String,
        getAllListingItemsSucceeded: (array: ArrayList<PropertyCardView>) -> Unit,
        getAllListingItemsFailed: () -> Unit) =
        repo.getAllListingItems(propertyType, listingType,
            getAllListingItemsSucceeded, getAllListingItemsFailed)
}