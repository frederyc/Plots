package com.example.plots.data

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class RemoteDataSourceFirebase {
    private val TAG = "RemoteDataSourceFirebase"

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val googleSignInClient = MutableLiveData<GoogleSignInClient>()
    private val authUserWithGoogleResult = MutableLiveData<Boolean>()
    private val createUserPersonalData = MutableLiveData<Boolean>()
    private val createUserResult = MutableLiveData<Boolean>()
    private val signInUserResult = MutableLiveData<Boolean>()

    fun signInWithGoogle(idToken: String) {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
            .addOnSuccessListener {
                Log.d(TAG, "signInWithGoogle: success")
                authUserWithGoogleResult.value = true
                db.collection("Accounts")
                    .add(Account("Google", auth.currentUser?.displayName,
                        auth.currentUser?.phoneNumber, auth.currentUser?.email))
                    .addOnSuccessListener {
                        Log.d(TAG, "createUserWithGoogle (account data): success")
                        createUserPersonalData.value = true
                    }.addOnFailureListener {
                        Log.w(TAG, "createUserWithGoogle (account data): failure: $it")
                        createUserPersonalData.value = false
                    }
            }.addOnFailureListener {
                Log.w(TAG, "signInWithGoogle: failure: $it")
                authUserWithGoogleResult.value = false
            }
    }

    fun getGoogleSignInClient(activity: Activity, defaultWebClientID: String):
            LiveData<GoogleSignInClient> {
        googleSignInClient.value = GoogleSignIn.getClient(activity,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(defaultWebClientID).requestEmail().build())
        return googleSignInClient
    }

    fun signInWithFacebook() {}     //TODO

    fun createUserWithEmail(name: String, phone: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d(TAG, "createUserWithEmail: success")
                createUserResult.value = true
                db.collection("Accounts")
                    .add(Account("Email", name, phone, email))
                    .addOnSuccessListener {
                        Log.d(TAG, "createUserWithEmail (account data): success")
                        createUserPersonalData.value = true
                    }.addOnFailureListener {
                        Log.w(TAG, "createUserWithEmail (account data): failure: $it")
                        createUserPersonalData.value = false
                    }
            }.addOnFailureListener {
                Log.w(TAG, "createUserWithEmail: failure: $it")
                createUserResult.value = false
            }

    }

    fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail: success")
                    signInUserResult.value = true
                }
                else {
                    Log.w(TAG, "signInWithEmail: failure", task.exception)
                    signInUserResult.value = false
                }
            }
    }

    fun getCreateUserWithEmailResult(): LiveData<Boolean> = createUserResult

    fun getSignInUserWithEmailResult(): LiveData<Boolean> = signInUserResult

    fun getCreateUserPersonalDataResult(): LiveData<Boolean> = createUserPersonalData

    fun getAuthUserWithGoogleResult(): LiveData<Boolean> = authUserWithGoogleResult
}