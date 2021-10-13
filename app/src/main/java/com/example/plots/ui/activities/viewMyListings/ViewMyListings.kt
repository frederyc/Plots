package com.example.plots.ui.activities.viewMyListings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plots.R
import com.example.plots.adapters.MyListingsRecyclerViewAdapter
import com.example.plots.databinding.ActivityViewMyListingsBinding
import com.example.plots.dialogs.ErrorDialog
import com.example.plots.dialogs.LoadingScreen
import com.example.plots.utils.AuthenticationInjector


class ViewMyListings : AppCompatActivity() {
    private val TAG = "ViewMyListings"

    private lateinit var binding: ActivityViewMyListingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMyListingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = AuthenticationInjector.provideViewMyListingsViewModelFactory()
        val viewModel: ViewMyListingsViewModel by viewModels { factory }

        val loadingScreen = LoadingScreen(this, R.layout.loading_screen)
        loadingScreen.start()

        viewModel.getCurrentUserListingItems({
            Log.d(TAG, "List size is: ${it.size}")
            Log.d(TAG, "List is: $it")

            if(it.size != 0) {
                binding.myListingsRecyclerView.visibility = View.VISIBLE
                binding.nothingAdded.visibility = View.GONE
                binding.myListingsRecyclerView.adapter = MyListingsRecyclerViewAdapter(it)
                binding.myListingsRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.myListingsRecyclerView.setHasFixedSize(true)
            }
            else {
                binding.myListingsRecyclerView.visibility = View.GONE
                binding.nothingAdded.visibility = View.VISIBLE
            }

            loadingScreen.end()
        }, {
            loadingScreen.end()
            ErrorDialog(this, R.layout.uploading_error)
        })

        initUI()
    }

    private fun initUI() {
        binding.toolbar.back.setOnClickListener { finish() }
    }
}