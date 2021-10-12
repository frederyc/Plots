package com.example.plots.ui.activities.createListing.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.plots.R
import com.example.plots.databinding.FragmentPropertyDescriptionBinding
import java.lang.StringBuilder

class FragmentPropertyDescription : Fragment(R.layout.fragment_property_description) {

    private val TAG = "FragmentPropertyDescription"
    private lateinit var binding: FragmentPropertyDescriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        propertyDescription = ""
        Log.d(TAG, "onCreate: started")
        Log.d(TAG, "onCreate: ended")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: started")
        Log.d(TAG, "onCreateView: ended")
        return inflater.inflate(R.layout.fragment_property_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: started")
        binding = FragmentPropertyDescriptionBinding.bind(view)
        binding.propertyDescription.editText?.text?.append(propertyDescription)
        Log.d(TAG, "onViewCreated: ended")
    }

    // To be used by activity code
    fun getDescription(): String = binding.propertyDescription.editText?.text.toString()

    override fun onDestroyView() {
        propertyDescription = binding.propertyDescription.editText?.text.toString()
        super.onDestroyView()
    }

    companion object {
        var propertyDescription = ""
    }

}