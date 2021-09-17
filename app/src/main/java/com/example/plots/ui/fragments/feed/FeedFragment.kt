package com.example.plots.ui.fragments.feed

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.plots.R

class FeedFragment : Fragment() {
    private val TAG = "FeedFragment"

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

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: started")
        Log.d(TAG, "onResume: ended")
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
}