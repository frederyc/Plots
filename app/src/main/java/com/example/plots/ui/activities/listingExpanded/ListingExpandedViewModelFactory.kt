package com.example.plots.ui.activities.listingExpanded

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plots.data.DatabaseRepository

class ListingExpandedViewModelFactory(private val repo: DatabaseRepository):
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListingExpandedViewModel(repo) as T
    }
}