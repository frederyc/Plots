package com.example.plots.ui.activities.updateCredentials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plots.data.DatabaseRepository

class AccountEditInformationViewModelFactory(private val repo: DatabaseRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AccountEditInformationViewModel(repo) as T
    }
}