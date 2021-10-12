package com.example.plots.ui.activities.createListing

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.example.plots.R
import com.example.plots.data.Property
import com.example.plots.databinding.ActivityCreateListingBinding
import com.example.plots.dialogs.ErrorDialog
import com.example.plots.dialogs.LoadingScreen
import com.example.plots.ui.activities.createListing.fragments.*
import com.example.plots.utils.AuthenticationInjector

class CreateListingActivity : AppCompatActivity() {

    private val TAG = "CreateListingActivity"

    private lateinit var binding: ActivityCreateListingBinding
    private lateinit var currentPage: Page

    private lateinit var fragmentPropertyType: FragmentPropertyType
    private lateinit var fragmentPropertyDetails: FragmentPropertyDetails
    private lateinit var fragmentPriceAndSize: FragmentPriceAndSize
    private lateinit var fragmentPropertyDescription: FragmentPropertyDescription
    private lateinit var fragmentPropertyPhotos: FragmentPropertyPhotos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateListingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentPropertyType = FragmentPropertyType()
        fragmentPropertyDetails = FragmentPropertyDetails()
        fragmentPriceAndSize = FragmentPriceAndSize()
        fragmentPropertyDescription = FragmentPropertyDescription()
        fragmentPropertyPhotos = FragmentPropertyPhotos()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragmentPropertyType).commit()
        currentPage = Page.PROPERTY_TYPE

        initUI()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        when(currentPage) {
            Page.PROPERTY_TYPE -> finish()
            Page.PROPERTY_DETAILS -> currentPage = Page.PROPERTY_TYPE
            Page.PROPERTY_PRICE -> currentPage = Page.PROPERTY_DETAILS
            Page.PROPERTY_DESCRIPTION -> currentPage = Page.PROPERTY_PRICE
            Page.PROPERTY_PHOTOS -> currentPage = Page.PROPERTY_DESCRIPTION
        }
        updatePage()
    }

    private fun initUI() {
        val factory = AuthenticationInjector.provideCreateListingActivityViewModelFactory()
        val viewModel: CreateListingActivityViewModel by viewModels { factory }

        binding.back.setOnClickListener { onBackPressed() }

        binding.next.setOnClickListener {
            when(currentPage) {
                Page.PROPERTY_TYPE -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragmentPropertyDetails)
                        .addToBackStack(null).commit()
                    currentPage = Page.PROPERTY_DETAILS
                }
                Page.PROPERTY_DETAILS -> {
                    supportFragmentManager
                        .beginTransaction().replace(R.id.fragment_container, fragmentPriceAndSize)
                        .addToBackStack(null).commit()
                    currentPage = Page.PROPERTY_PRICE
                }
                Page.PROPERTY_PRICE -> {
                    supportFragmentManager
                        .beginTransaction().replace(R.id.fragment_container, fragmentPropertyDescription)
                        .addToBackStack(null).commit()
                    currentPage = Page.PROPERTY_DESCRIPTION
                }
                Page.PROPERTY_DESCRIPTION -> {
                    supportFragmentManager
                        .beginTransaction().replace(R.id.fragment_container, fragmentPropertyPhotos)
                        .addToBackStack(null).commit()
                    currentPage = Page.PROPERTY_PHOTOS
                }
                Page.PROPERTY_PHOTOS -> {
                    val loadingScreen = LoadingScreen(this, R.layout.uploading_loading_screen)
                    loadingScreen.start()

                    val listingTypeArray = fragmentPriceAndSize.getPriceAndSize()
                    val property = Property(null, null,
                        fragmentPropertyType.getPropertyType(),
                        fragmentPropertyDetails.getAmenities().toMap(),
                        if(listingTypeArray[0] == 0) "Sale" else "Rent",
                        listingTypeArray[1], listingTypeArray[2], listingTypeArray[3],
                        listingTypeArray[4], listingTypeArray[5],
                        fragmentPropertyDescription.getDescription())

                        viewModel.uploadPropertyToDatabase(
                            property,
                            fragmentPropertyPhotos.getImageUris(), {
                                loadingScreen.end()
                                finish()
                            }, {
                                Toast.makeText(baseContext, "Failed", Toast.LENGTH_LONG).show()
                                loadingScreen.end()
                                ErrorDialog(this, R.layout.uploading_error).start()
                            })
                }
            }
            updatePage()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updatePage() {
        binding.page.text = "${currentPage.ordinal + 1}/5"
        if(currentPage == Page.PROPERTY_PHOTOS)
            binding.next.text = "Finish"
        if(currentPage != Page.PROPERTY_PHOTOS && binding.next.text == "Finish")
            binding.next.text = "Next"
    }

    private enum class Page {
        PROPERTY_TYPE, PROPERTY_DETAILS, PROPERTY_PRICE, PROPERTY_DESCRIPTION, PROPERTY_PHOTOS
    }

}