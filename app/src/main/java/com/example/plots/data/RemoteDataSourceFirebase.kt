package com.example.plots.data

import android.app.Activity
import android.app.TaskInfo
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
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
                    .whereEqualTo("signInType", "Google")
                    .whereEqualTo("email", auth.currentUser?.email)
                    .get().addOnSuccessListener {
                        if(it.documents.size == 0)
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
                        else
                            Log.d(TAG, "User ${auth.currentUser?.email} already has a Google " +
                                    "account, signing them in")
                    }.addOnFailureListener {
                        Log.d(TAG, "Failed to fetch specific account from remote db: $it")
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

    //todo This is crap, change it
    fun loadProfileImage(activity: Activity, imageHolder: ImageView) {
        db.collection("Accounts")
            .whereEqualTo("email", auth.currentUser?.email).get()
            .addOnSuccessListener {
                when(it.size()) {
                    0 -> Log.w(TAG, "No account related to this email found")
                    1 -> {
                        when(it.documents[0].data?.get("signInType") as String) {
                            "Google" -> {
                                Log.d(TAG, "SignInType: Google")
                                Glide.with(activity).load(auth.currentUser?.photoUrl).into(imageHolder)
                            }
                            "Email" -> {
                                Log.d(TAG, "SignInType: Email")
                            }
                            else -> Log.w(TAG, "No sign in type detected")
                        }
                    }
                    else -> Log.w(TAG, "Multiple accounts related to this email found")
                }
            }.addOnFailureListener {
                Log.w(TAG, "Querying accounts for loading profile image failed: $it")
            }
    }

    fun signOutUser() = auth.signOut()

    fun getCreateUserWithEmailResult(): LiveData<Boolean> = createUserResult

    fun getSignInUserWithEmailResult(): LiveData<Boolean> = signInUserResult

    fun getCreateUserPersonalDataResult(): LiveData<Boolean> = createUserPersonalData

    fun getAuthUserWithGoogleResult(): LiveData<Boolean> = authUserWithGoogleResult
}