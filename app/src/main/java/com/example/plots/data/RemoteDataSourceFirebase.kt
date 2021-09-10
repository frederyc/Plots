package com.example.plots.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RemoteDataSourceFirebase {
    private val TAG = "RemoteDataSourceFirebase"

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var createUserPersonalData = MutableLiveData<Boolean>()
    private var createUserResult = MutableLiveData<Boolean>()
    private var signInUserResult = MutableLiveData<Boolean>()

    fun signInWithGoogle() {}       //TODO

    fun signInWithFacebook() {}     //TODO

    fun createUserWithEmail(name: String, phone: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d(TAG, "createUserWithEmail: success")
                createUserResult.value = true
                db.collection("Accounts").add(Account("Email", name, phone, email))
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

}