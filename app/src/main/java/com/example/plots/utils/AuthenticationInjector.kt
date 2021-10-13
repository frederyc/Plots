package com.example.plots.utils

import com.example.plots.data.AuthenticationRepository
import com.example.plots.data.DatabaseRepository
import com.example.plots.data.RemoteDataSourceFirebase
import com.example.plots.ui.activities.createListing.CreateListingActivityViewModelFactory
import com.example.plots.ui.activities.listingExpanded.ListingExpandedViewModel
import com.example.plots.ui.activities.listingExpanded.ListingExpandedViewModelFactory
import com.example.plots.ui.activities.updateCredentials.AccountEditInformationViewModelFactory
import com.example.plots.ui.activities.viewMyListings.ViewMyListingsViewModelsFactory
import com.example.plots.ui.fragments.account.AccountViewModelFactory
import com.example.plots.ui.login.LoginViewModelFactory
import com.example.plots.ui.register.RegisterViewModelFactory

object AuthenticationInjector {

    fun provideLoginViewModelFactory(): LoginViewModelFactory {
        val repo = AuthenticationRepository.getInstance(RemoteDataSourceFirebase())
        return LoginViewModelFactory(repo)
    }

    fun provideRegisterViewModelFactory(): RegisterViewModelFactory {
        val repo = AuthenticationRepository.getInstance(RemoteDataSourceFirebase())
        return RegisterViewModelFactory(repo)
    }

    fun provideAccountViewModelFactory(): AccountViewModelFactory {
        val repo = DatabaseRepository.getInstance(RemoteDataSourceFirebase())
        return AccountViewModelFactory(repo)
    }

    fun provideAccountEditInformationViewModelFactory(): AccountEditInformationViewModelFactory {
        val repo = DatabaseRepository.getInstance(RemoteDataSourceFirebase())
        return AccountEditInformationViewModelFactory(repo)
    }

    fun provideCreateListingActivityViewModelFactory(): CreateListingActivityViewModelFactory {
        val repo = DatabaseRepository.getInstance(RemoteDataSourceFirebase())
        return CreateListingActivityViewModelFactory(repo)
    }

    fun provideViewMyListingsViewModelFactory(): ViewMyListingsViewModelsFactory {
        val repo = DatabaseRepository.getInstance(RemoteDataSourceFirebase())
        return ViewMyListingsViewModelsFactory(repo)
    }

    fun provideListingExpandedViewModelFactory(): ListingExpandedViewModelFactory {
        val repo = DatabaseRepository.getInstance(RemoteDataSourceFirebase())
        return ListingExpandedViewModelFactory(repo)
    }

}