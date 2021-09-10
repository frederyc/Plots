package com.example.plots.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class RemoteDataSourceFirebase {
    private val TAG = "RemoteDataSourceFirebase"
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private var createUserResult = MutableLiveData<Boolean>()
    private var signInUserResult = MutableLiveData<Boolean>()

    fun signInWithGoogle() {}       //TODO

    fun signInWithFacebook() {}     //TODO

    fun createUserWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail: success")
                    createUserResult.value = true
                }
                else {
                    Log.w(TAG, "createUserWithEmail: failure", task.exception)
                    createUserResult.value = false
                }
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

}