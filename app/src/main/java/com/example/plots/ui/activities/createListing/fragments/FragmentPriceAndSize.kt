package com.example.plots.ui.activities.createListing.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.plots.R
import com.example.plots.databinding.FragmentPriceAndSizeBinding

class FragmentPriceAndSize : Fragment(R.layout.fragment_price_and_size) {

    private val TAG = "FragmentPriceAndSize"

    private lateinit var binding: FragmentPriceAndSizeBinding
    private var bedroomsNumber: Int = 0
    private var bathroomsNumber: Int = 0
    private var kitchensNumber: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_price_and_size, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPriceAndSizeBinding.bind(view)
        initUI()
    }

    // To be used by activity code
    fun getPriceAndSize(): ArrayList<Int> {
        val priceString = binding.price.editText?.text.toString()
        val surfaceString = binding.suface.editText?.text.toString()

        return arrayListOf(
            if (binding.forSaleRadioButton.isChecked) 1 else 0,
            if(priceString == "") 0 else priceString.toInt(),
            if(surfaceString == "") 0 else surfaceString.toInt(),
            bedroomsNumber, bathroomsNumber, kitchensNumber
        )
    }

    private fun initUI() {
        binding.forSaleRadioButton.isChecked = true

        binding.bedrooms.setLabelFormatter {
            return@setLabelFormatter when(it) {
                6.0.toFloat() -> "${it.toInt()}+"
                else -> "${it.toInt()}"
            }
        }

        binding.bathrooms.setLabelFormatter {
            return@setLabelFormatter when(it) {
                6.0.toFloat() -> "${it.toInt()}+"
                else -> "${it.toInt()}"
            }
        }

        binding.kitchens.setLabelFormatter {
            return@setLabelFormatter when(it) {
                3.0.toFloat() -> "${it.toInt()}+"
                else -> "${it.toInt()}"
            }
        }

        binding.bedrooms.addOnChangeListener { _, value, _ -> bedroomsNumber = value.toInt() }

        binding.bathrooms.addOnChangeListener { _, value, _ -> bathroomsNumber = value.toInt() }

        binding.kitchens.addOnChangeListener { _, value, _ -> kitchensNumber = value.toInt() }

        binding.forSaleRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                binding.listingType.setImageResource(R.drawable.for_sale)
        }

        binding.forRentRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                binding.listingType.setImageResource(R.drawable.for_rent)
        }

    }

}