package com.example.plots.ui.activities.createListing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plots.data.DatabaseRepository

class CreateListingActivityViewModelFactory(private val repo: DatabaseRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreateListingActivityViewModel(repo) as T
    }
}