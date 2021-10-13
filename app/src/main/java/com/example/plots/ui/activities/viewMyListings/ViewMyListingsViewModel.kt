package com.example.plots.ui.activities.viewMyListings

import androidx.lifecycle.ViewModel
import com.example.plots.data.DatabaseRepository
import com.example.plots.data.PropertyCardView

class ViewMyListingsViewModel(private val repo: DatabaseRepository) : ViewModel() {

    fun getCurrentUserListingItems(
        getCurrentUserListingItemsSucceeded: (array: ArrayList<PropertyCardView>) -> Unit,
        getCurrentUserListingItemsFailed: () -> Unit) =
        repo.getCurrentUserListingItems(getCurrentUserListingItemsSucceeded,
            getCurrentUserListingItemsFailed)

}