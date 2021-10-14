package com.example.plots.ui.fragments.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plots.data.DatabaseRepository

class FeedFragmentViewModelFactory(private val repo: DatabaseRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FeedFragmentViewModel(repo) as T
    }
}