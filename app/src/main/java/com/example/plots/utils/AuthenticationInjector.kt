package com.example.plots.utils

import com.example.plots.data.AuthenticationRepository
import com.example.plots.data.RemoteDataSourceFirebase
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

}