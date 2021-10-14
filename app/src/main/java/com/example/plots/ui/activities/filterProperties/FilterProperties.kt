package com.example.plots.ui.activities.filterProperties

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import com.example.plots.databinding.ActivityFilterPropertiesBinding

class FilterProperties : AppCompatActivity() {
    private val TAG = "FilterProperties"

    private lateinit var binding: ActivityFilterPropertiesBinding
    private lateinit var typeRadioGroup: ArrayList<RadioButton>
    private lateinit var listingRadioGroup: ArrayList<RadioButton>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterPropertiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        typeRadioGroup = arrayListOf(binding.anyPropertyTypeRadioButton, binding.houseRadioButton,
            binding.townHouseRadioButton, binding.condoRadioButton, binding.landRadioButton)

        listingRadioGroup = arrayListOf(binding.anyListingTypeRadioButton,
            binding.forSaleRadioButton, binding.forRentRadioButton)

        initUI()

    }

    private fun initUI() {
        when(propertyType) {
            "" -> binding.anyPropertyTypeRadioButton.isChecked = true
            "House" -> binding.houseRadioButton.isChecked = true
            "Townhouse" -> binding.townHouseRadioButton.isChecked = true
            "Condo" -> binding.condoRadioButton.isChecked = true
            "Land" -> binding.landRadioButton.isChecked = true
        }

        when(listingType) {
            "" -> binding.anyListingTypeRadioButton.isChecked = true
            "For Sale" -> binding.forSaleRadioButton.isChecked = true
            "For Rent" -> binding.forRentRadioButton.isChecked = true
        }

        binding.toolbar.back.setOnClickListener {
            propertyType = ""
            listingType = ""
            finish()
        }

        for(i in typeRadioGroup)
            typeRadioGroupHandler(i)

        for(i in listingRadioGroup)
            listingRadioGroupHandler(i)

        binding.filter.setOnClickListener {
            if(propertyType == "Town House")
                propertyType = "Townhouse"
            if(listingType == "Town House")
                listingType = "Townhouse"
            finish()
        }
    }

    private fun typeRadioGroupHandler(b: RadioButton) {
        b.setOnClickListener {
            propertyType = if(b.text == "Any") "" else b.text.toString()
            Log.d(TAG, "type Test: $propertyType")
            for(i in typeRadioGroup)
                i.isChecked = i.text == b.text
        }
    }

    private fun listingRadioGroupHandler(b: RadioButton) {
        b.setOnClickListener {
            listingType = if(b.text == "Any") "" else b.text.toString()
            Log.d(TAG, "listing Test: $listingType")
            for(i in listingRadioGroup)
                i.isChecked = i.text == b.text
        }
    }

    companion object {
        var propertyType = ""
        var listingType = ""
    }
}