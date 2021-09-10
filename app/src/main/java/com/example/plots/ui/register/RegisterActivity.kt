package com.example.plots.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.plots.databinding.ActivityRegisterBinding
import com.example.plots.utils.AuthenticationInjector
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private val TAG = "RegisterActivity"

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initUI()
    }

    private fun initUI() {
        val factory = AuthenticationInjector.provideRegisterViewModelFactory()
        val viewModel: RegisterViewModel by viewModels { factory }

        binding.signIn.setOnClickListener {
            finish()
        }

        binding.signUp.setOnClickListener {
            if(checkUserDataForUpload()) {
                viewModel.createUserWithEmail(binding.email.text.toString(),
                    binding.password.text.toString())
                viewModel.getCreateUserWithEmailResult().observe(this, Observer {
                    if(it) {
                        Log.d(TAG, "User registered successfully")
                        finish()
                    }
                    else {
                        Log.w(TAG, "User failed to register")
                        Toast.makeText(baseContext, "User failed to register", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

//    private fun createUserWithEmailAndPassword() {
//        auth.createUserWithEmailAndPassword(
//            binding.email.text.toString(), binding.password.text.toString())
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    val user = auth.currentUser
//                    Toast.makeText(this,
//                        "Account created successfully",
//                        Toast.LENGTH_SHORT).show()
//                    Log.d(TAG, "Account created successfully")
//                    finish()
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "createUserWithEmail: failure", task.exception)
//                    Toast.makeText(baseContext, "Sign Up failed.",
//                        Toast.LENGTH_SHORT).show()
//                }
//            }
//    }

    private fun checkUserDataForUpload(): Boolean {
        if(binding.email.text.toString().isEmpty()) {
            binding.email.error = "Please enter email"
            binding.email.requestFocus()
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()) {
            binding.email.error = "Please enter a valid email address"
            binding.email.requestFocus()
            return false
        }
        if(binding.password.text.toString().isEmpty()) {
            binding.password.error = "Please enter a password"
            binding.password.requestFocus()
            return false
        }
        if(binding.confirmPassword.text.toString().isEmpty()) {
            binding.confirmPassword.error = "Please confirm your password"
            binding.confirmPassword.requestFocus()
            return false
        }
        if(binding.confirmPassword.text.toString() != binding.password.text.toString()) {
            binding.confirmPassword.error = "Passwords must match"
            binding.confirmPassword.requestFocus()
            return false
        }
        return true
    }

}

//
//
//class SignUpActivity : AppCompatActivity() {
//    private val TAG = "SignUpActivity"
//
//    private lateinit var imageUri: Uri
//    private lateinit var storageReference: StorageReference
//    private lateinit var imageKey: UUID
//    private lateinit var database: FirebaseFirestore
//    private lateinit var auth: FirebaseAuth
//    private var imageSelected: Boolean = false
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sign_up)
//        Log.d(TAG, "onCreate: started")
//
//        storageReference = FirebaseStorage.getInstance().reference
//        database = FirebaseFirestore.getInstance()
//        auth = FirebaseAuth.getInstance()
//
//        choosePhoto.setOnClickListener {
//            choosePicture()
//        }
//
//        signUp.setOnClickListener {
//            if(checkRegistration()) {
//                uploadPicture(imageUri)
//                val account = Account(imageKey.toString(), firstNameSignUp.text.toString(),
//                    lastNameSignUp.text.toString(), phoneNumberSignUp.text.toString(),
//                    emailSignUp.text.toString(), encodePasswordMD5(passwordSignUp.text.toString()))
//                uploadAccountData(account)
//                finish()
//            }
//        }
//
//        Log.d(TAG, "onCreate: ended")
//
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == 1 && resultCode == -1 && data != null) {
//            imageUri = data.data!!
//            profileImageSignUp.setImageURI(imageUri)
//            imageSelected = true
//        }
//    }
//
//    private fun choosePicture() {
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//        startActivityForResult(intent, 1)
//    }
//
//    private fun uploadPicture(uri: Uri) {
//        imageKey = UUID.randomUUID()
//        val reference = storageReference.child("ProfileImages/$imageKey")
//        reference.putFile(uri).addOnSuccessListener {
//            Log.d(TAG, "Image uploaded successfully")
//        }.addOnFailureListener {
//            Log.e(TAG, it.toString())
//        }
//
//        // If there are no errors, signup the user
//        auth.createUserWithEmailAndPassword(
//            emailSignUp.text.toString(),
//            passwordSignUp.text.toString()).addOnCompleteListener(this) { task ->
//            if (task.isSuccessful) {
//                // Sign in success, update UI with the signed-in user's information
//                val user = auth.currentUser
//                Toast.makeText(
//                    this, "Account created successfully", Toast.LENGTH_SHORT).show()
//                finish()
//            } else {
//                // If sign in fails, display a message to the user.
//                Log.w(TAG, "createUserWithEmail: failure", task.exception)
//                Toast.makeText(baseContext, "Sign Up failed.",
//                    Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun uploadAccountData(newAccount: Account) {
//        database.collection("Accounts").add(newAccount)
//            .addOnSuccessListener {
//                Log.d(TAG, "Account was successfully added to the cloud")
//            }.addOnFailureListener {
//                Log.e(TAG, it.toString())
//            }
//    }
//
//    private fun checkRegistration(): Boolean {
//        if(!imageSelected) {
//            choosePhoto.error = "Please selected a profile picture"
//            choosePhoto.requestFocus()
//            return false
//        }
//        if(firstNameSignUp.text.isEmpty()) {
//            firstNameSignUp.error = "Please enter your first name"
//            firstNameSignUp.requestFocus()
//            return false
//        }
//        if(lastNameSignUp.text.isEmpty()) {
//            lastNameSignUp.error = "Please enter your last name"
//            lastNameSignUp.requestFocus()
//            return false
//        }
//        if(phoneNumberSignUp.text.isEmpty()) {
//            phoneNumberSignUp.error = "Please enter your phone number"
//            phoneNumberSignUp.requestFocus()
//            return false
//        }
//        if(emailSignUp.text.toString().isEmpty()) {
//            emailSignUp.error = "Please enter email"
//            emailSignUp.requestFocus()
//            return false
//        }
//        if(!Patterns.EMAIL_ADDRESS.matcher(emailSignUp.text.toString()).matches()) {
//            emailSignUp.error = "Please enter a valid email address"
//            emailSignUp.requestFocus()
//            return false
//        }
//        if(passwordSignUp.text.toString().isEmpty()) {
//            passwordSignUp.error = "Please enter a password"
//            passwordSignUp.requestFocus()
//            return false
//        }
//        if(confirmPasswordSignUp.text.toString().isEmpty()) {
//            confirmPasswordSignUp.error = "Please confirm your password"
//            confirmPasswordSignUp.requestFocus()
//            return false
//        }
//        if(passwordSignUp.text.toString() != confirmPasswordSignUp.text.toString()) {
//            confirmPasswordSignUp.error = "Passwords must match"
//            confirmPasswordSignUp.requestFocus()
//            return false
//        }
//        return true
//    }
//
//    private fun encodePasswordMD5(password: String): String? {
//        return try {
//            val md = MessageDigest.getInstance("MD5")
//            md.update(password.toByteArray())
//            val bytes = md.digest()
//            val sb = StringBuilder()
//            for (aByte in bytes) sb.append(
//                ((aByte and 0xff.toByte()) + 0x100).toString(16).substring(1)
//            )
//            sb.toString()
//        } catch (e: NoSuchAlgorithmException) {
//            e.printStackTrace()
//            return null
//        }
//    }
//
//}