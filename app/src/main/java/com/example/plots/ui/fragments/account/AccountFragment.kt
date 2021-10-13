package com.example.plots.ui.fragments.account

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.plots.databinding.FragmentAccountBinding
import com.example.plots.R
import com.example.plots.ui.login.LoginActivity
import com.example.plots.utils.AuthenticationInjector
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.plots.dialogs.LoadingScreen
import com.example.plots.ui.activities.createListing.CreateListingActivity
import com.example.plots.ui.activities.updateCredentials.AccountEditInformation
import com.example.plots.ui.activities.viewMyListings.ViewMyListings

class AccountFragment : Fragment(R.layout.fragment_account) {
    private val TAG = "AccountFragment"

    private lateinit var binding: FragmentAccountBinding
    private lateinit var googleSignInActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var resultContracts: ActivityResultLauncher<Intent>

    private var name = String()
    private var email = String()
    private var phone = String()
    private var profileImageUri: Uri? = null            // Used to retrieve google profile image
    private var profileImageBitmap: Bitmap? = null      // User to retrieve email profile image

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: started")
        super.onCreate(savedInstanceState)

        val factory = AuthenticationInjector.provideAccountViewModelFactory()
        val viewModel: AccountViewModel by viewModels { factory }

        resultContracts =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                Activity.RESULT_OK -> {
                    viewModel.getUserName().observe(this, { userName ->
                        name = userName
                        loadUserNameToUI()
                        Log.d(TAG, "Finished loading name")
                    })
                    viewModel.getUserPhone().observe(this, { userPhone ->
                        phone = userPhone
                        loadUserPhoneToUI()
                        Log.d(TAG, "Finished loading phone")
                    })
                    viewModel.getProfileImage({
                        Log.w(TAG, "Shouldn't be possible")
                    }, { bitmap ->
                        if(bitmap != null) {
                            profileImageBitmap = bitmap
                            loadUserProfileImage()
                        }
                        Log.d(TAG, "Finished loading profile photo")
                    }, {
                        Log.w(TAG, "Error loading profile image")
                    })
                }
                Activity.RESULT_CANCELED -> Log.d(TAG, "No data to update")
                else -> Log.w(TAG, "Update credentials" +
                        " activity returned unknown code: ${it.resultCode}")
            }
        }

        getUserData()

        Log.d(TAG, "onCreate: ended")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: started")
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ended")
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: started")
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountBinding.bind(view)
        loadUserDataToUI()
        initButtonBehaviour()
        Log.d(TAG, "onViewCreated: ended")
    }

    private fun getUserData() {
        Log.d(TAG, "getUserData: started")
        val factory = AuthenticationInjector.provideAccountViewModelFactory()
        val viewModel: AccountViewModel by viewModels { factory }
        val loadingScreen = LoadingScreen(activity as Activity, R.layout.loading_screen)
        loadingScreen.start()
        viewModel.getUserName().observe(this, {
            name = it
            loadUserNameToUI()
            Log.d(TAG, "Finished loading name")
        })
        viewModel.getUserPhone().observe(this, {
            phone = it
            loadUserPhoneToUI()
            Log.d(TAG, "Finished loading phone")
        })
        viewModel.getUserEmail().observe(this, {
            email = it
            loadUserEmailToUI()
            Log.d(TAG, "Finished loading email")
        })
        viewModel.getProfileImage({
            profileImageUri = it
            loadUserProfileImage()
            loadingScreen.end()
            Log.d(TAG, "Finished loading google profile image")
        }, {
            profileImageBitmap = it
            Log.d(TAG, "BITMAP TEST: $profileImageBitmap")
            loadUserProfileImage()
            loadingScreen.end()
            Log.d(TAG, "Finished loading email profile image")
        }, {
            Log.w(TAG, "failed to retrieve profile image")
            loadingScreen.end()
        })

        Log.d(TAG, "getUserData: ended")
    }
    
    private fun loadUserDataToUI() {
        loadUserNameToUI()
        loadUserEmailToUI()
        loadUserPhoneToUI()
        loadUserProfileImage()
    }

    private fun loadUserNameToUI() { binding.name.text = name }
    private fun loadUserEmailToUI() { binding.email.text = email }
    private fun loadUserPhoneToUI() { binding.phone.text = phone }
    private fun loadUserProfileImage() {
        if(profileImageUri != null)
            Glide.with(activity as Activity).load(profileImageUri).into(binding.profileImage)
        else if(profileImageBitmap != null)
            binding.profileImage.setImageBitmap(profileImageBitmap)
    }

    private fun initButtonBehaviour() {
        val factory = AuthenticationInjector.provideAccountViewModelFactory()
        val viewModel: AccountViewModel by viewModels { factory }

        binding.signOut.setOnClickListener {
            viewModel.signOutUser()
            activity?.finish()
            startActivity(Intent(activity?.baseContext, LoginActivity::class.java))
        }

        binding.updateCredentials.setOnClickListener {
            resultContracts.launch(Intent(context, AccountEditInformation::class.java))
        }

        binding.createListing.setOnClickListener {
            startActivity(Intent(context, CreateListingActivity::class.java))
        }

        binding.viewMyListings.setOnClickListener {
            startActivity(Intent(context, ViewMyListings::class.java))
        }

    }

}