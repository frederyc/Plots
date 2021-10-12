package com.example.plots.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.plots.R
import com.example.plots.databinding.ActivityMainBinding
import com.example.plots.ui.fragments.account.AccountFragment
import com.example.plots.ui.fragments.favorites.FavoritesFragment
import com.example.plots.ui.fragments.feed.FeedFragment
import com.example.plots.ui.fragments.settings.SettingsFragment
import com.example.plots.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import java.util.*

/**
 * TODO: When back is pressed, the fragment changes
 * TODO: Stop changing fragment when current fragment is used
 */

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    private lateinit var feedFragment: FeedFragment
    private lateinit var favoritesFragment: FavoritesFragment
    private lateinit var accountFragment: AccountFragment
    private lateinit var settingsFragment: SettingsFragment
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: started")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        if(auth.currentUser == null) {
            Log.d(TAG, "User is not logged in")
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }
        else
            Log.d(TAG, "User: ${auth.currentUser?.email}")

        feedFragment = FeedFragment()
        favoritesFragment = FavoritesFragment()
        accountFragment = AccountFragment()
        settingsFragment = SettingsFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, feedFragment).commit()

        initNavigationToolbar()

        Log.d(TAG, "onCreate: ended")
    }

    private fun initNavigationToolbar() {

        binding.bottomNavigation.setOnItemSelectedListener {
            Log.d(TAG, "setOnItemSelectedListener: called")
            val selectedFragment: Fragment? = when (it.itemId) {
                R.id.nav_feed -> feedFragment
                R.id.nav_favorites -> favoritesFragment
                R.id.nav_account -> accountFragment
                R.id.nav_settings -> settingsFragment
                else -> null
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment!!)
                .addToBackStack(null).commit()

            Log.d(TAG, "setOnItemSelectedListener: ended")

            return@setOnItemSelectedListener true
        }

    }

    override fun onBackPressed() {
        if(binding.bottomNavigation.selectedItemId == R.id.nav_feed) {
            super.onBackPressed()
            finish()
        }
        else
            binding.bottomNavigation.selectedItemId = R.id.nav_feed
    }

    private fun printBackStack() {
        for(i in 0 until supportFragmentManager.backStackEntryCount)
            Log.d(TAG, "${supportFragmentManager.getBackStackEntryAt(i)}")
    }

    private fun test() {

    }

}