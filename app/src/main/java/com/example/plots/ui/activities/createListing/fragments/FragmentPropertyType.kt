package com.example.plots.ui.activities.createListing.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.example.plots.R
import com.example.plots.databinding.FragmentPropertyTypeBinding

class FragmentPropertyType : Fragment(R.layout.fragment_property_type) {

    private val TAG = "FragmentPropertyType"

    private lateinit var binding: FragmentPropertyTypeBinding
    private lateinit var radioGroup: ArrayList<RadioButton>

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: started")
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ended")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: started")
        Log.d(TAG, "onCreateView: ended")
        return inflater.inflate(R.layout.fragment_property_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: started")
        binding = FragmentPropertyTypeBinding.bind(view)

        radioGroup = arrayListOf(binding.houseRadioButton, binding.townHouseRadioButton,
            binding.condoRadioButton, binding.landRadioButton)
        radioGroup[0].isChecked = true

        initUI()

        Log.d(TAG, "onViewCreated: ended")
    }

    // To be used by activity code
    fun getPropertyType(): String = when(propertyType) {
        PropertyType.HOUSE -> "House"
        PropertyType.TOWNHOUSE -> "Townhouse"
        PropertyType.CONDO -> "Condo"
        PropertyType.LAND -> "Land"
    }

    override fun onResume() {
        super.onResume()
        for(i in radioGroup)
            if(i.isChecked) {
                exchangeImageAndUpdateType(i)
                break
            }
    }

    private fun initUI() {
        for(i in radioGroup)
            radioGroupHandler(i)
    }

    private fun radioGroupHandler(b: RadioButton) {
        b.setOnClickListener {
            for(i in radioGroup)
                i.isChecked = i.text == b.text
            exchangeImageAndUpdateType(b)
        }
    }

    private fun exchangeImageAndUpdateType(b: RadioButton) {
        when(b.text.toString()) {
            "House" -> {
                binding.propertyTypeImage.setImageResource(R.drawable.house_icon)
                propertyType = PropertyType.HOUSE
            }
            "Town House" -> {
                binding.propertyTypeImage.setImageResource(R.drawable.duplex_icon)
                propertyType = PropertyType.TOWNHOUSE
            }
            "Condo" -> {
                binding.propertyTypeImage.setImageResource(R.drawable.condo_icon)
                propertyType = PropertyType.CONDO
            }
            "Land" -> {
                binding.propertyTypeImage.setImageResource(R.drawable.land_icon)
                propertyType = PropertyType.LAND
            }
        }
    }

    companion object {
        var propertyType = PropertyType.HOUSE
        enum class PropertyType { HOUSE, TOWNHOUSE, CONDO, LAND }
    }

}