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
import com.bumptech.glide.Glide
import com.example.plots.databinding.FragmentAccountBinding
import com.example.plots.R
import com.example.plots.activities.MainActivity
import com.example.plots.ui.login.LoginActivity
import com.example.plots.utils.AuthenticationInjector
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class AccountFragment : Fragment(R.layout.fragment_account) {
    private val TAG = "AccountFragment"

    private lateinit var binding: FragmentAccountBinding

    private var name = String()
    private var email = String()
    private var phone = String()

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
        loadProfileImage()
        initButtonBehaviour()
        Log.d(TAG, "onViewCreated: ended")
    }

    private fun getUserData() {
        Log.d(TAG, "getUserData: started")
        val factory = AuthenticationInjector.provideAccountViewModelFactory()
        val viewModel: AccountViewModel by viewModels { factory }
        viewModel.getUserName().observe(this, {
            name = it
            loadUserDataToUI()
        })
        viewModel.getUserPhone().observe(this, {
            phone = it
            loadUserDataToUI()
        })
        viewModel.getUserEmail().observe(this, {
            email = it
            loadUserDataToUI()          // Observation is async,
                                        // that's why we call it inside every read
        })
        Log.d(TAG, "getUserData: ended")
    }

    private fun loadUserDataToUI() {
        binding.name.text = name                                                // This doesn't, make it, check dependencies between viewModels and repositories
        binding.email.text = FirebaseAuth.getInstance().currentUser?.email      // This works
        binding.phone.text = phone
    }

    private fun loadProfileImage() {
        val factory = AuthenticationInjector.provideAccountViewModelFactory()
        val viewModel: AccountViewModel by viewModels { factory }
        viewModel.loadProfileImage(activity as Activity, binding.profileImage)
        // Glide.with(activity as Activity).load(FirebaseAuth.getInstance().currentUser?.photoUrl).into(binding.profileImage)
    }

    private fun initButtonBehaviour() {
        val factory = AuthenticationInjector.provideAccountViewModelFactory()
        val viewModel: AccountViewModel by viewModels { factory }
        binding.signOut.setOnClickListener {
            viewModel.signOutUser()
            startActivity(Intent(activity?.baseContext, LoginActivity::class.java))
            activity?.finish()
        }
    }

}