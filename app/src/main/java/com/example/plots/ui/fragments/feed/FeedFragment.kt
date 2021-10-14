package com.example.plots.ui.fragments.feed

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plots.R
import com.example.plots.adapters.MyListingsRecyclerViewAdapter
import com.example.plots.databinding.FragmentFeedBinding
import com.example.plots.dialogs.LoadingScreen
import com.example.plots.ui.activities.filterProperties.FilterProperties
import com.example.plots.utils.AuthenticationInjector

class FeedFragment : Fragment() {
    private val TAG = "FeedFragment"
    private lateinit var binding: FragmentFeedBinding

    private var propertyType = ""
    private var listingType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: started")
        Log.d(TAG, "onCreate: ended")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: started")
        Log.d(TAG, "onCreateView: ended")
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFeedBinding.bind(view)

        val factory = AuthenticationInjector.provideFeedFragmentViewModelFactory()
        val viewModel: FeedFragmentViewModel by viewModels { factory }

        initUI()
    }

    override fun onResume() {
        super.onResume()
        propertyType = FilterProperties.propertyType
        listingType = FilterProperties.listingType

        val factory = AuthenticationInjector.provideFeedFragmentViewModelFactory()
        val viewModel: FeedFragmentViewModel by viewModels { factory }

        val loadingScreen = LoadingScreen(activity as Activity, R.layout.loading_screen)
        loadingScreen.start()

        viewModel.getAllListingItems(propertyType, listingType, {
            if(it.size != 0) {
                binding.listingsRecyclerView.visibility = View.VISIBLE
                binding.nothingAdded.visibility = View.GONE
                binding.listingsRecyclerView.adapter = MyListingsRecyclerViewAdapter(it)
                binding.listingsRecyclerView.layoutManager = LinearLayoutManager(context)
                binding.listingsRecyclerView.setHasFixedSize(true)
            }
            else {
                binding.listingsRecyclerView.visibility = View.GONE
                binding.nothingAdded.visibility = View.VISIBLE
            }
            loadingScreen.end()
        }, {
            Log.w(TAG, "Error retrieving list")
            loadingScreen.end()
        })
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: started")
        Log.d(TAG, "onPause: ended")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: started")
        Log.d(TAG, "onDestroyView: ended")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: started")
        Log.d(TAG, "onDestroy: ended")
    }

    private fun initUI() {
        binding.toolbar.filter.setOnClickListener {
            startActivity(Intent(context, FilterProperties::class.java))
        }
    }

}