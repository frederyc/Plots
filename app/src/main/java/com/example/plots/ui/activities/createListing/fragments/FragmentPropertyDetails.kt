package com.example.plots.ui.activities.createListing.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import com.example.plots.databinding.FragmentPropertyDetailsBinding
import android.view.View
import android.view.ViewGroup
import com.example.plots.R

class FragmentPropertyDetails : Fragment(R.layout.fragment_property_details) {

    private val TAG = "FragmentPropertyDetails"
    private lateinit var binding: FragmentPropertyDetailsBinding
    private var parkingSpacesValue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: started")
        Log.d(TAG, "onCreate: ended")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: started")
        Log.d(TAG, "onCreateView: ended")
        return inflater.inflate(R.layout.fragment_property_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: started")
        binding = FragmentPropertyDetailsBinding.bind(view)

        initUI()

        Log.d(TAG, "onViewCreated: ended")
    }

    // To be used by activity code
    fun getAmenities(): MutableMap<String, Int> {
        val map = mutableMapOf<String, Int>()

        map["Road access"] = boolToInt(binding.roadAccess.isChecked)
        map["Water"] = boolToInt(binding.water.isChecked)
        map["Sewerage"] = boolToInt(binding.sewerage.isChecked)
        map["AC"] = boolToInt(binding.ac.isChecked)
        map["Gas"] = boolToInt(binding.gas.isChecked)
        map["Electricity"] = boolToInt(binding.electricity.isChecked)
        map["Internet"] = boolToInt(binding.internet.isChecked)

        map["Parking spaces"] = parkingSpacesValue
        map["Garage"] = boolToInt(binding.garage.isChecked)
        map["Balcony"] = boolToInt(binding.balcony.isChecked)

        map["Community pool"] = boolToInt(binding.communityPool.isChecked)
        map["Private pool"] = boolToInt(binding.privatePool.isChecked)
        map["Gym"] = boolToInt(binding.gym.isChecked)
        map["Gated access"] = boolToInt(binding.gatedAccess.isChecked)
        map["Panoramic view"] = boolToInt(binding.panoramicView.isChecked)
        map["eCar charging station"] = boolToInt(binding.eCarChargingStation.isChecked)
        map["Online payments"] = boolToInt(binding.onlinePayments.isChecked)

        return map
    }

    private fun boolToInt(b: Boolean) = if(b) 1 else 0

    private fun initUI() {
        binding.parkingSpaces.setLabelFormatter {
            return@setLabelFormatter when (it) {
                4.0.toFloat() -> "${it.toInt()}+"
                0.0.toFloat() -> "No parking spaces"
                else -> "${it.toInt()}"
            }
        }

        binding.parkingSpaces.addOnChangeListener { _, value, _ ->
            parkingSpacesValue = value.toInt()
        }
    }

}