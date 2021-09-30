package com.example.plots.data

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("LABEL_NAME_CLASH")
class RemoteDataSourceFirebase {
    private val TAG = "RemoteDataSourceFirebase"

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val googleSignInClient = MutableLiveData<GoogleSignInClient>()

    fun signInWithGoogle(idToken: String, signInWithGoogleSucceeded: () -> Unit,
        signInWithGoogleFailed: () -> Unit) {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
            .addOnCompleteListener {
                if(it.exception != null) {
                    Log.w(TAG, "signInWithGoogle: failure: ${it.exception}")
                    signInWithGoogleFailed()
                }
                else {
                    Log.d(TAG, "signInWithGoogle: success")
                    signInWithGoogleSucceeded()

                    db.collection("Accounts")
                        .whereEqualTo("signInType", "Google")
                        .whereEqualTo("email", auth.currentUser?.email).get()
                        .addOnSuccessListener { querySnapshot ->
                            if(querySnapshot.documents.size == 0)
                                db.collection("Accounts")
                                    .add(Account("Google", auth.currentUser?.displayName,
                                        auth.currentUser?.phoneNumber, auth.currentUser?.email))
                                    .addOnSuccessListener {
                                        Log.d(TAG, "createUserWithGoogle (account data): success")
                                    }.addOnFailureListener { e ->
                                        Log.w(TAG, "createUserWithGoogle (account data): " +
                                                "failure: $e")
                                    }
                            else
                                Log.d(TAG, "User ${auth.currentUser?.email} already has a Google " +
                                        "account, signing them in")
                        }.addOnFailureListener { e ->
                            Log.w(TAG, "Failed to fetch specific account from remote db: $e")
                        }

                }
            }
    }

    fun getGoogleSignInClient(activity: Activity, defaultWebClientID: String):
            LiveData<GoogleSignInClient> {
        googleSignInClient.value = GoogleSignIn.getClient(activity,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(defaultWebClientID).requestEmail().build())
        return googleSignInClient
    }

    fun createUserWithEmail(name: String, phone: String, email: String, password: String,
        onAccountCreationSuccess: () -> Unit, onAccountCreationFailure: () -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.exception != null) {
                    Log.w(TAG, "createUserWithEmail: failure: ${it.exception}")
                    onAccountCreationFailure()
                }
                else {
                    Log.d(TAG, "createUserWithEmail: success")
                    auth.signOut()
                    onAccountCreationSuccess()
                    db.collection("Accounts")
                        .add(Account("Email", name, phone, email))
                        .addOnSuccessListener {
                            Log.d(TAG, "createUserWithEmail (account data): success")
                        }.addOnFailureListener {
                            Log.w(TAG, "createUserWithEmail (account data): failure: $it")
                        }
                }
            }
    }

    fun signInWithEmail(email: String, password: String, signInWithEmailSucceeded: () -> Unit,
        signInWithEmailFailed: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.exception != null) {
                    signInWithEmailFailed()
                    Log.w(TAG, "signInWithEmail failed: ${it.exception}")
                }
                else {
                    signInWithEmailSucceeded()
                    Log.d(TAG, "signInWithEmail succeeded")
                }
            }
    }

    fun getUserEmail(): LiveData<String> = MutableLiveData(auth.currentUser?.email)

    fun getUserName(): LiveData<String> = MutableLiveData<String>().apply {
        db.collection("Accounts")
            .whereEqualTo("email", auth.currentUser?.email).get()
            .addOnSuccessListener {
                if(it.documents[0].data?.get("email") == auth.currentUser?.email)
                    this.value = it.documents[0].data?.get("name").let { name ->
                        if(name != null) name as String else "No name found"
                    }
                else
                    Log.w(TAG, "No Firestore data related to account found")
            }.addOnFailureListener {
                Log.w(TAG, "Failed to retrieve Firestore data for account: $it")
            }
    }

    fun getUserPhone(): LiveData<String> = MutableLiveData<String>().apply {
        db.collection("Accounts")
            .whereEqualTo("email", auth.currentUser?.email).get()
            .addOnSuccessListener {
                //todo remove double checking
                if(it.documents[0].data?.get("email") == auth.currentUser?.email)
                    this.value = it.documents[0].data?.get("phone").let { phone ->
                        if(phone != null) phone as String else "No phone number found"
                    }
                else
                    Log.w(TAG, "No Firestore data related to account found")
            }.addOnFailureListener {
                Log.w(TAG, "Failed to retrieve Firestore data for account: $it")
            }
    }

    fun getProfileImageUri(): LiveData<Uri>? {
        val imageUri = MutableLiveData<Uri>()
        db.collection("Accounts").whereEqualTo("email", auth.currentUser?.email)
            .get().addOnSuccessListener {
                when(it.size()) {
                    0 -> Log.w(TAG, "No account related to this email found")
                    1 -> {
                        when(it.documents[0].data?.get("signInType") as String) {
                            "Google" -> {
                                Log.d(TAG, "SignInType: Google")
                                if(auth.currentUser?.photoUrl == null)
                                    Log.d(TAG, "Google user doesn't have a profile image")
                                else
                                    imageUri.value = auth.currentUser?.photoUrl
                            }
                            "Email" -> {
                                Log.d(TAG, "SignInType: Email")
                                //TODO
                            }
                            else -> {
                                Log.w(TAG, "No sign in type detected")
                            }
                        }
                    }
                    else -> Log.w(TAG, "Multiple accounts related to this email found")
                }
            }.addOnFailureListener {
                Log.w(TAG, "Failed to find user: $it")
            }
        return if(auth.currentUser?.photoUrl == null) null else imageUri
    }

    fun signOutUser() = auth.signOut()
}