package com.example.plots.ui.activities.updateCredentials

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.plots.R
import com.example.plots.databinding.ActivityAccountEditInformationBinding
import com.example.plots.dialogs.LoadingScreen
import com.example.plots.utils.AuthenticationInjector

class AccountEditInformation : AppCompatActivity() {
    private val TAG = "AccountEditInformation"

    private lateinit var binding: ActivityAccountEditInformationBinding
    private lateinit var resultContract: ActivityResultLauncher<Intent>

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: started")
        binding = ActivityAccountEditInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resultContract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                Log.d(TAG, "result code: ${it.resultCode}")
                if(it.resultCode == Activity.RESULT_OK) {
                    imageUri = it.data?.data
                    binding.profileImg.setImageURI(imageUri)
                }
        }

        initUI()

        Log.d(TAG, "onCreate: ended")
    }

    private fun initUI() {
        val factory = AuthenticationInjector.provideAccountEditInformationViewModelFactory()
        val viewModel: AccountEditInformationViewModel by viewModels { factory }
        viewModel.getUserSignInType({
            val loadingScreen = LoadingScreen(this, R.layout.loading_screen)
            loadingScreen.start()
            when(it) {
                "Google" -> {
                    binding.constraintLayout.visibility = View.GONE
                    loadingScreen.end()
                }
                "Email" -> {
                    binding.constraintLayout.visibility = View.VISIBLE
                    viewModel.getProfileImage( {
                        Log.w(TAG, "This should be impossible 1")
                        loadingScreen.end()
                    }, { bitmap ->
                        if(bitmap != null)
                            binding.profileImg.setImageBitmap(bitmap)
                        else
                            Log.d(TAG, "No profile image related to account found")
                        loadingScreen.end()
                    }, {
                        Log.w(TAG, "Failed to retrieve profile image from firebase")
                        loadingScreen.end()
                    })
                }
                else -> {
                    Log.e(TAG, "Unknown sign in type detected")
                    loadingScreen.end()
                }
            }
        }, {
            Toast.makeText(baseContext, "An internal error has occurred", Toast.LENGTH_LONG)
                .show()
        })

        binding.updateCredentials.setOnClickListener {
            val loadingScreen = LoadingScreen(this, R.layout.loading_screen)
            loadingScreen.start()

            if(binding.phone.text.isNotEmpty() && binding.name.text.isNotEmpty()) {
                viewModel.updateUserName(binding.name.text.toString(), {
                    Log.d(TAG, "updateName: succeeded")
                    viewModel.updateUserPhoneNumber(binding.phone.text.toString(), {
                        Log.d(TAG, "updatePhoneNumber: succeeded")
                        Toast.makeText(baseContext, "Name and phone number update: succeeded",
                            Toast.LENGTH_SHORT).show()
                        if(imageUri != null) {
                            viewModel.uploadProfileImage(imageUri!!, {
                                // Profile image update succeeded
                                Log.d(TAG, "Update profile image: succeeded")
                                setResult(Activity.RESULT_OK)
                                finish()
                            }, {
                                // Profile image update failed
                                Log.w(TAG, "Update profile image: failed")
                                Toast.makeText(baseContext, "Upload profile image: failed",
                                    Toast.LENGTH_LONG).show()
                                loadingScreen.end()
                            })
                        }
                    }, {
                        Log.w(TAG, "updatePhoneNumber: failed")
                        Toast.makeText(baseContext, "Phone number update: failed",
                            Toast.LENGTH_LONG).show()
                        loadingScreen.end()
                    })
                }, {
                    Log.w(TAG, "updateName: failed")
                    Toast.makeText(baseContext, "Name update: failed",
                        Toast.LENGTH_LONG).show()
                    loadingScreen.end()
                })
            }
            else if(binding.name.text.isNotEmpty()) {
                viewModel.updateUserName(binding.name.text.toString(), {
                    // Name update succeeded
                    Log.d(TAG, "updateName: succeeded")
                    Toast.makeText(baseContext, "Name update: succeeded",
                        Toast.LENGTH_SHORT).show()
                    loadingScreen.end()
                    if(imageUri != null) {
                        viewModel.uploadProfileImage(imageUri!!, {
                            // Profile image update succeeded
                            Log.d(TAG, "Update profile image: succeeded")
                            setResult(Activity.RESULT_OK)
                            finish()
                        }, {
                            // Profile image update failed
                            Log.w(TAG, "Update profile image: failed")
                            Toast.makeText(baseContext, "Upload profile image: failed",
                                Toast.LENGTH_LONG).show()
                            loadingScreen.end()
                        })
                    }
                }, {
                    // Name update failed
                    Log.w(TAG, "updateName: failed")
                    Toast.makeText(baseContext, "Name update: failed",
                        Toast.LENGTH_LONG).show()
                    loadingScreen.end()
                })
            }
            else if(binding.phone.text.isNotEmpty()) {
                viewModel.updateUserPhoneNumber(binding.phone.text.toString(), {
                    // Phone number update succeeded
                    Log.d(TAG, "updatePhoneNumber: succeeded")
                    Toast.makeText(baseContext, "Phone number update: succeeded",
                        Toast.LENGTH_SHORT).show()
                    loadingScreen.end()
                    if(imageUri != null) {
                        viewModel.uploadProfileImage(imageUri!!, {
                            // Profile image update succeeded
                            Log.d(TAG, "Update profile image: succeeded")
                            setResult(Activity.RESULT_OK)
                            finish()
                        }, {
                            // Profile image update failed
                            Log.w(TAG, "Update profile image: failed")
                            Toast.makeText(baseContext, "Upload profile image: failed",
                                Toast.LENGTH_LONG).show()
                            loadingScreen.end()
                        })
                    }
                }, {
                    // Phone number update failed
                    Log.w(TAG, "updatePhoneNumber: failed")
                    Toast.makeText(baseContext, "Phone number update: failed",
                        Toast.LENGTH_LONG).show()
                    loadingScreen.end()
                } )
            }
            else {
                if(imageUri != null) {
                    viewModel.uploadProfileImage(imageUri!!, {
                        // Profile image update succeeded
                        Log.d(TAG, "Update profile image: succeeded")
                        setResult(Activity.RESULT_OK)
                        Toast.makeText(baseContext, "Profile image updated",
                            Toast.LENGTH_SHORT).show()
                        finish()
                    }, {
                        // Profile image update failed
                        Log.w(TAG, "Update profile image: failed")
                        Toast.makeText(baseContext, "Upload profile image: failed",
                            Toast.LENGTH_LONG).show()
                        loadingScreen.end()
                    })
                }
                else {
                    Toast.makeText(baseContext, "Nothing to update", Toast.LENGTH_SHORT).show()
                    loadingScreen.end()
                    finish()
                }
            }

        }

        binding.uploadImage.setOnClickListener {
            pickImageFromGallery()
        }
        /**
         * TODO:
         * - load already set profile image (if exists) in AccountEditInformation
         * - delete old image when uploading new one
         * - load profile image in AccountFragment after uploading
         */

    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultContract.launch(intent)
    }

}