package com.example.plots.ui.activities.viewMyListings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plots.data.DatabaseRepository

class ViewMyListingsViewModelsFactory(private val repo: DatabaseRepository):
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewMyListingsViewModel(repo) as T
    }

}