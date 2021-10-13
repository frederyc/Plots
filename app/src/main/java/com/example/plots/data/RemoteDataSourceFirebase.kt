package com.example.plots.data

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.ArrayList

@Suppress("LABEL_NAME_CLASH")
class RemoteDataSourceFirebase {
    private val TAG = "RemoteDataSourceFirebase"

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storageRef: StorageReference = FirebaseStorage.getInstance().reference

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

    fun getProfileImage(getGoogleProfileImageSucceeded: (imageUri: Uri) -> Unit,
                        getEmailProfileImageSucceeded: (bitmap: Bitmap?) -> Unit,
                        getProfileImageFailure: () -> Unit) {
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
                                    getGoogleProfileImageSucceeded(auth.currentUser?.photoUrl!!)
                            }
                            "Email" -> {
                                Log.d(TAG, "SignInType: Email")
                                try {
                                    val id = it.documents[0].data?.get("profileImageKey") as String
                                    val file = File.createTempFile("tempFile", "")
                                    storageRef.child("ProfileImages/$id").getFile(file)
                                        .addOnCompleteListener { downloadTask ->
                                            if (downloadTask.exception != null) {
                                                Log.w(
                                                    TAG,
                                                    "Retrieve email profile image: failed: " +
                                                            "${downloadTask.exception}"
                                                )
                                                getProfileImageFailure()
                                            } else {
                                                Log.d(
                                                    TAG,
                                                    "Retrieve email profile image: succeeded"
                                                )
                                                val bitmap =
                                                    BitmapFactory.decodeFile(file.absolutePath)
                                                getEmailProfileImageSucceeded(bitmap)
                                            }
                                        }
                                }
                                catch (e: NullPointerException) {
                                    Log.d(TAG, "No email profile image found")
                                    getEmailProfileImageSucceeded(null)
                                }
                            }
                            else -> {
                                Log.w(TAG, "No sign in type detected")
                                getProfileImageFailure()
                            }
                        }
                    }
                    else -> {
                        Log.w(TAG, "Multiple accounts related to this email found")
                        getProfileImageFailure()
                    }
                }
            }.addOnFailureListener {
                Log.w(TAG, "Failed to find user: $it")
                getProfileImageFailure()
            }
    }

    fun getUserSignInType(getUserSignInTypeSucceeded: (signInType: String) -> Unit,
                          getUserSignInTypeFailure: () -> Unit) {
        db.collection("Accounts").whereEqualTo("email", auth.currentUser?.email)
            .get().addOnCompleteListener {
                if(it.exception != null) {
                    Log.w(TAG, "getUserSignInType: failed")
                    getUserSignInTypeFailure()
                }
                else if(it.result.documents.size != 1) {
                    Log.w(TAG, "getUserSignInType: multiple users found with same email")
                    getUserSignInTypeFailure()
                }
                else {
                    Log.d(TAG, "getUserSignInType: succeeded")
                    val signInType = it.result.documents[0].data?.get("signInType") as String
                    getUserSignInTypeSucceeded(signInType)
                }
            }
    }

    fun updateUserPhoneNumber(newPhoneNumber: String, updateUserPhoneNumberSucceeded: () -> Unit,
                              updateUserPhoneNumberFailure: () -> Unit) {
        db.collection("Accounts").whereEqualTo("email", auth.currentUser?.email)
            .get()
            .addOnCompleteListener {
                if(it.exception != null) {
                    Log.w(TAG, "updateUserPhoneNumber: failure: ${it.exception}")
                    updateUserPhoneNumberFailure()
                }
                else if(it.result.documents.size != 1) {
                    Log.w(TAG, "updateUserPhoneNumber: multiple users found with same email")
                    updateUserPhoneNumberFailure()
                }
                else {
                    it.result.documents[0].reference.update("phone", newPhoneNumber)
                        .addOnCompleteListener() { task ->
                            if(task.exception != null) {
                                Log.w(TAG, "updateUserPhoneNumber: failure: ${task.exception}")
                                updateUserPhoneNumberFailure()
                            }
                            else {
                                Log.d(TAG, "updateUserPhoneNumber: succeeded")
                                updateUserPhoneNumberSucceeded()
                            }
                        }
                }
            }
    }

    fun updateUserName(newName: String, updateUserNameSucceeded: () -> Unit,
                       updateUserNameFailure: () -> Unit ) {
        db.collection("Accounts").whereEqualTo("email", auth.currentUser?.email)
            .get()
            .addOnCompleteListener {
                if(it.exception != null) {
                    Log.w(TAG, "updateUserName: failure: ${it.exception}")
                    updateUserNameFailure()
                }
                else if(it.result.documents.size != 1) {
                    Log.w(TAG, "updateUserName: multiple users found with same email")
                    updateUserNameFailure()
                }
                else {
                    it.result.documents[0].reference.update("name", newName)
                        .addOnCompleteListener { task ->
                            if(task.exception != null) {
                                Log.w(TAG, "updateUserName: failure: ${task.exception}")
                                updateUserNameFailure()
                            }
                            else {
                                Log.d(TAG, "updateUserName: succeeded")
                                updateUserNameSucceeded()
                            }
                        }
                }
            }
    }

    fun uploadProfileImage(uri: Uri, uploadProfileImageSucceeded: () -> Unit,
                           uploadProfileImageFailed: () -> Unit) {
        val key = UUID.randomUUID()
        var oldKey: String?
        db.collection("Accounts").whereEqualTo("email", auth.currentUser?.email)
            .get()
            .addOnCompleteListener {
                if(it.exception != null) {
                    Log.w(TAG, "uploadProfileImage: failed (at finding related account)")
                    uploadProfileImageFailed()
                }
                else if(it.result.documents.size != 1) {
                    Log.w(TAG, "uploadProfileImage: multiple users found with same email")
                    uploadProfileImageFailed()
                }
                else {
                    /*  Check if there is already a profile photo related to account and delete it
                        when changing the profile photo */
                    it.result.documents[0].reference.get()
                        .addOnCompleteListener {  task ->
                            if(task.exception != null) {
                                Log.w(TAG, "uploadProfileImage: failed (at retrieving old" +
                                        "image key reference): ${task.exception}")
                                uploadProfileImageFailed()
                            }
                            else {
                                oldKey = task.result.get("profileImageKey") as String?
                                if(oldKey != null)
                                    storageRef.child("ProfileImages/$oldKey.jpeg").delete()
                                        .addOnCompleteListener { imageDeletion ->
                                            if(imageDeletion.exception != null) {
                                                Log.w(TAG, "Failed to delete old profile" +
                                                        "image: ${imageDeletion.exception}")
                                                uploadProfileImageFailed()
                                            }
                                            else
                                                Log.d(TAG, "Image with id=$oldKey was " +
                                                        "successfully deleted")
                                        }
                            }
                        }

                    // Update 'profileImageKey' path in FireStore record related to current account
                    it.result.documents[0].reference.update("profileImageKey", key.toString())
                        .addOnCompleteListener { task ->
                            if(task.exception != null) {
                                Log.w(TAG, "uploadProfileImage: failed " +
                                        "(at adding image key reference)")
                                uploadProfileImageFailed()
                            }
                            else
                                Log.d(TAG, "uploadProfileImage: key set succeeded")
                        }
                }
            }

        // Upload new profile image to FireStore
        storageRef.child("ProfileImages/$key").putFile(uri)
            .addOnCompleteListener {
                if(it.exception != null) {
                    Log.w(TAG, "Upload profile image: failed: ${it.exception}")
                    uploadProfileImageFailed()
                }
                else {
                    Log.d(TAG, "Upload profile image: succeeded")
                    uploadProfileImageSucceeded()
                }
            }
    }

    fun signOutUser() = auth.signOut()

    fun getPropertyInformationById(propertyId: String,
        getPropertyInformationByIdSucceeded: (property: Property?) -> Unit,
        getPropertyInformationByIdFailed: () -> Unit
    ) {
        val property = Property()
        db.collection("Properties").document(propertyId)
            .get()
            .addOnCompleteListener {
                if(it.exception != null) {
                    Log.w(TAG, "Couldn't find property: ${it.exception}")
                    getPropertyInformationByIdFailed()
                }
                else {
                    val res = it.result
                    property.propertyType = res.get("propertyType") as String
                    property.amenities = res.get("amenities") as Map<String, Int>
                    property.listingType = res.get("listingType") as String
                    property.price = (res.get("price") as Long).toInt()
                    property.surface = (res.get("surface") as Long).toInt()
                    property.bedrooms = (res.get("bedrooms") as Long).toInt()
                    property.bathrooms = (res.get("bathrooms") as Long).toInt()
                    property.kitchens = (res.get("kitchens") as Long).toInt()
                    property.description = res.get("description") as String
                    getPropertyInformationByIdSucceeded(property)
                }
            }
    }

    fun getPropertyOwnersInformationById(propertyId: String,
        getPropertyOwnersInformationByIdSucceeded: (ownerData: OwnerData?) -> Unit,
        getPropertyOwnersInformationByIdFailed: () -> Unit
    ) {
        db.collection("Properties").document(propertyId)
            .get()
            .addOnCompleteListener {
                if(it.exception != null) {
                    Log.w(TAG, "Couldn't find property: ${it.exception}")
                    getPropertyOwnersInformationByIdFailed()
                }
                else {
                    db.collection("Accounts")
                        .document(it.result.get("ownerKey") as String)
                        .get()
                        .addOnCompleteListener { task ->
                            if(task.exception != null) {
                                Log.w(TAG, "Couldn't find account related to properiety: ${it.exception}")
                                getPropertyOwnersInformationByIdFailed()
                            }
                            else {
                                val ownerData = OwnerData(
                                    task.result.get("name") as String,
                                    task.result.get("email") as String,
                                    task.result.get("phone") as String,
                                    task.result.get("signInType") as String
                                )
                                getPropertyOwnersInformationByIdSucceeded(ownerData)
                            }
                        }
                }
            }
    }

    fun getAllPhotosOfProperty(propertyId: String,
        getAllPhotosOfPropertySucceeded: (bitmaps: ArrayList<Bitmap>) -> Unit,
        getAllPhotosOfPropertyFailed: () -> Unit) {

        db.collection("Properties").document(propertyId)
            .get()
            .addOnCompleteListener {
                if(it.exception != null) {
                    Log.w(TAG, "Failed to find specific property: ${it.exception}")
                    getAllPhotosOfPropertyFailed()
                }
                else {
                    val photoCollectionKey = it.result.data?.get("photosCollectionKey") as String
                    val photoPathMap = it.result.data?.get("imageKeys") as Map<*, *>
                    val photoPathArray = ArrayList<String>()
                    photoPathMap.forEach { mapEntry ->
                        photoPathArray.add(mapEntry.value as String)
                    }

                    if(photoPathArray.size == 0) {
                        // No photos related to property found
                        getAllPhotosOfPropertySucceeded(ArrayList())
                        Log.d(TAG, "No images related to property found")
                    }
                    else {
                        val bitmaps = ArrayList<Bitmap>()
                        loadImagesIntoList(0, photoCollectionKey, bitmaps, photoPathArray) { bitmaps_ ->
                            getAllPhotosOfPropertySucceeded(bitmaps_)
                        }
                    }
                }
            }
    }

    private fun loadImagesIntoList(cnt: Int, collectionKey: String, listToAdd: ArrayList<Bitmap>,
                                   list: ArrayList<String>, callBackFnc: (array: ArrayList<Bitmap>) -> Unit)
    {
        if(cnt < list.size) {
            val file = File.createTempFile("tempFile", "")
            storageRef.child("PropertyImages/$collectionKey/${list[cnt]}").getFile(file)
                .addOnCompleteListener {
                    if(it.exception != null)
                        Log.e(TAG, "Failed to download image: ${it.exception}")
                    else {
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        listToAdd.add(bitmap)
                        Log.d(TAG, "cnt = $cnt")
                        loadImagesIntoList(cnt + 1, collectionKey, listToAdd, list, callBackFnc)
                    }
                }
        }
        else {
            Log.d(TAG, "ended fnc")
            callBackFnc(listToAdd)
            return
        }

    }

    fun uploadPropertyToDatabase(
        property: Property,
        uris: ArrayList<Uri?>,
        uploadPropertyToDatabaseSucceeded: () -> Unit,
        uploadPropertyToDatabaseFailed: () -> Unit) {
        getAccountFirestoreId( { accountKey ->
            Log.d(TAG, "Succeeded to get account fireStore id")

            uploadPropertyImagesToStorage(uris, { photosCollectionKey, imageKeys ->
                Log.d(TAG, "Succeeded to upload property images to storage")
                property.ownerKey = accountKey
                property.photosCollectionKey = photosCollectionKey
                property.imageKeys = arrayToMap(imageKeys)
                db.collection("Properties").add(property)
                    .addOnCompleteListener {
                        if(it.exception != null) {
                            Log.w(TAG, "Failed to upload property to firebase: ${it.exception}")
                            uploadPropertyToDatabaseFailed()
                        }
                        else {
                            Log.d(TAG, "Succeeded uploading property to firebase")
                            uploadPropertyToDatabaseSucceeded()
                        }
                    }
            }, {
                Log.w(TAG, "Failed to upload property images")
                uploadPropertyToDatabaseFailed()
            })
        }, {
            Log.w(TAG, "Failed to get account firestore id")
            uploadPropertyToDatabaseFailed()
        })
    }

    fun getCurrentUserListingItems(
        getCurrentUserListingItemsSucceeded: (array: ArrayList<PropertyCardView>) -> Unit,
        getCurrentUserListingItemsFailed: () -> Unit) {
        getAccountFirestoreId({
            db.collection("Properties").whereEqualTo("ownerKey", it)
                .get()
                .addOnCompleteListener { task ->
                    if(task.exception != null) {
                        Log.w(TAG, "Failed to retrieve owner key: ${task.exception}")
                        getCurrentUserListingItemsFailed()
                    }
                    else {
                        val list = ArrayList<PropertyCardView>()
                        if(task.result.documents.size == 0)
                            getCurrentUserListingItemsSucceeded(ArrayList())
                        else {
                            loadPropertiesIntoList(0, list, task.result.documents) {
                                getCurrentUserListingItemsSucceeded(
                                    list
                                )
                            }
                        }
                    }
                }
        }, {
            Log.w(TAG, "failed to retrieve account firestore id")
            getCurrentUserListingItemsFailed()
        })
    }

    private fun loadPropertiesIntoList(cnt: Int, listToAdd: ArrayList<PropertyCardView>,
        list: List<DocumentSnapshot>, callBackFnc: (array: ArrayList<PropertyCardView>) -> Unit)
    {
        if(cnt < list.size) {
            getFirstImageOfProperty(list[cnt].id, {
                val i = list[cnt]
                listToAdd.add(PropertyCardView(
                    i.id, it, i.get("listingType") as String,
                    (i.get("price") as Long).toInt(),
                    (i.get("surface") as Long).toInt(),
                    (i.get("bedrooms") as Long).toInt(),
                    (i.get("bathrooms") as Long).toInt(),
                    (i.get("kitchens") as Long).toInt())
                )

                loadPropertiesIntoList(cnt + 1, listToAdd, list, callBackFnc)
            }, {
                Log.e(TAG, "some error has occurred")
            })
        }
        else {
            callBackFnc(listToAdd)
            return
        }
    }

    private fun getFirstImageOfProperty(propertyId: String,
            getFirstImageOfPropertySucceeded: (imageBitmap: Bitmap?) -> Unit,
            getFirstImageOfPropertyFailed: () -> Unit) {
        db.collection("Properties").document(propertyId)
            .get()
            .addOnCompleteListener {
                if(it.exception != null) {
                    Log.w(TAG, "Failed to find property: ${it.exception}")
                    getFirstImageOfPropertyFailed()
                }
                else {
                    val collectionPath = it.result.data?.get("photosCollectionKey") as String?
                    val firstImagePathMap = (it.result.data?.get("imageKeys") as Map<*, *>?)
                    var firstImagePath: String? = null

                    if(firstImagePathMap != null)
                        firstImagePath = firstImagePathMap["0"] as String

                    val file = File.createTempFile("tempFile", "")
                    if(collectionPath != null && firstImagePath != null) {
                        storageRef.child("PropertyImages/$collectionPath/$firstImagePath")
                            .getFile(file)
                            .addOnCompleteListener { task ->
                                if(task.exception != null) {
                                    Log.w(TAG, "Failed to download first image photo: ${task.exception}")
                                    getFirstImageOfPropertyFailed()
                                }
                                else {
                                    Log.d(TAG, "Successfully retrieved first image")
                                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                    getFirstImageOfPropertySucceeded(bitmap)
                                }
                            }
                    }
                    else {
                        Log.d(TAG, "No image to retrieve")
                        getFirstImageOfPropertySucceeded(null)
                    }
                }
            }
    }

    private fun arrayToMap(array: ArrayList<String>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for(i in 0 until array.size)
            map[i.toString()] = array[i]
        Log.d(TAG, "ARRAY TO MAP TEST: $map")
        return map
    }

    private fun uploadPropertyImagesToStorage(
        uris: ArrayList<Uri?>,
        uploadPropertyImagesToStorageSucceeded: (storageID: String, imageKeys: ArrayList<String>) -> Unit,
        uploadPropertyImagesToStorageFailed: () -> Unit) {
        val key = UUID.randomUUID()
        val imageKeys = ArrayList<String>()
        // Upload images to storage
        uris.forEach {
            val imageKey = UUID.randomUUID()
            imageKeys.add(imageKey.toString())
            storageRef.child("PropertyImages/$key/$imageKey").putFile(it!!)
                .addOnCompleteListener { task ->
                    if(task.exception != null) {
                        Log.w(TAG, "uploadPropertyImagesToStorageFailed: ${task.exception}")
                        uploadPropertyImagesToStorageFailed()
                    }
                    else {
                        Log.d(TAG, "Uploading image with id: $imageKey succeeded")
                        if(it == uris.last()) {
                            Log.d(TAG, "TEST IMAGE KEYS: $imageKeys")
                            uploadPropertyImagesToStorageSucceeded(key.toString(), imageKeys)
                        }
                    }
                }
        }
    }

    private fun getAccountFirestoreId(
        getAccountFirestoreIdSucceeded: (id: String) -> Unit,
        getAccountFirestoreIdFailed: () -> Unit) {
        db.collection("Accounts").whereEqualTo("email", auth.currentUser?.email)
            .get().addOnCompleteListener {
                if(it.exception != null) {
                    Log.w(TAG, "getAccountFirestoreIdFailed: ${it.exception}")
                    getAccountFirestoreIdFailed()
                }
                else
                    when(it.result.documents.size) {
                        1 -> {
                            Log.d(TAG, "getAccountFirestoreIdSucceeded")
                            val id = it.result.documents[0].id
                            getAccountFirestoreIdSucceeded(id)
                        }
                        else -> {
                            Log.w(TAG, "Multiple or none fireStore instances related to account found")
                            getAccountFirestoreIdFailed()
                        }
                    }
            }
    }

}