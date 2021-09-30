package com.example.plots.ui.fragments.account

import android.app.Activity
import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.plots.dialogs.LoadingScreen
import com.example.plots.ui.login.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

class AccountFragment : Fragment(R.layout.fragment_account) {
    private val TAG = "AccountFragment"

    private lateinit var binding: FragmentAccountBinding
    private lateinit var googleSignInActivityLauncher: ActivityResultLauncher<Intent>

    private var name = String()
    private var email = String()
    private var phone = String()
    private var profileImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: started")
        super.onCreate(savedInstanceState)
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
        val loadingScreen =
            LoadingScreen(activity as Activity, R.layout.loading_screen).also { it.start() }
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

        //todo change this
        viewModel.getProfileImageUri()?.let {
            it.observe(this, { uri ->
                profileImageUri = uri
                loadUserProfileImage()
                loadingScreen.end()
                Log.d(TAG, "Finished loading image")
            })
        }
        if(viewModel.getProfileImageUri() == null)
            loadingScreen.end()
        //todo change this

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
        profileImageUri?.let {
            Glide.with(activity as Activity).load(it).into(binding.profileImage)
        }
    }

    private fun initButtonBehaviour() {
        val factory = AuthenticationInjector.provideAccountViewModelFactory()
        val viewModel: AccountViewModel by viewModels { factory }
        binding.signOut.setOnClickListener {
            viewModel.signOutUser()
            activity?.finish()
            startActivity(Intent(activity?.baseContext, LoginActivity::class.java))
        }
    }

}