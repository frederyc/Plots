package com.example.plots.data

import android.app.Activity
import androidx.lifecycle.LiveData
import com.google.android.gms.auth.api.signin.GoogleSignInClient

class AuthenticationRepository private constructor(
    private val dataSrc: RemoteDataSourceFirebase) {

    fun signInWithEmail(email: String, password: String, signInWithEmailSucceeded: () -> Unit,
                        signInWithEmailFailed: () -> Unit) {
        dataSrc.signInWithEmail(email, password, signInWithEmailSucceeded,
            signInWithEmailFailed)
    }

    fun createUserWithEmail(name: String, phone: String, email: String, password: String,
                            onAccountCreationSuccess: () -> Unit,
                            onAccountCreationFailure: () -> Unit) {
        dataSrc.createUserWithEmail(name, phone, email, password,
            onAccountCreationSuccess, onAccountCreationFailure)
    }

    fun signInWithGoogle(idToken: String, signInWithGoogleSucceeded: () -> Unit,
                         signInWithGoogleFailed: () -> Unit) {
        dataSrc.signInWithGoogle(idToken, signInWithGoogleSucceeded, signInWithGoogleFailed)
    }

    fun getGoogleSignInClient(activity: Activity, defaultWebClientID: String):
            LiveData<GoogleSignInClient> =
        dataSrc.getGoogleSignInClient(activity, defaultWebClientID)

    companion object {
        @Volatile private var instance: AuthenticationRepository? = null

        fun getInstance(dataSrc: RemoteDataSourceFirebase) =
            instance ?: synchronized(this) {
                instance ?: AuthenticationRepository(dataSrc).also {
                    instance = it
                }
            }
    }

}