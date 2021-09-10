package com.example.plots.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.plots.databinding.ActivityMainBinding
import com.example.plots.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // For test purposes only
        val auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null) {
            Log.d(TAG, "Logged in detected. Signing out")
            auth.signOut()
        }
        // For test purposes only

        binding.button.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }
}