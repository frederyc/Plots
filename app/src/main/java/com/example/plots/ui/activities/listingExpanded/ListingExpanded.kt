package com.example.plots.ui.activities.listingExpanded

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.plots.R
import com.example.plots.data.OwnerData
import com.example.plots.data.Property
import com.example.plots.databinding.ActivityListingExpandedBinding
import com.example.plots.dialogs.LoadingScreen
import com.example.plots.ui.fragments.account.AccountFragment
import com.example.plots.utils.AuthenticationInjector
import com.google.firebase.auth.FirebaseAuth
import java.lang.StringBuilder

class ListingExpanded() : AppCompatActivity() {
    private val TAG = "ListingExpanded"

    private lateinit var propertyId: String
    private lateinit var binding: ActivityListingExpandedBinding
    private lateinit var ownerData: OwnerData
    private lateinit var propertyData: Property

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListingExpandedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        propertyId = intent.getStringExtra("propertyId")!!

        val factory = AuthenticationInjector.provideListingExpandedViewModelFactory()
        val viewModel: ListingExpandedViewModel by viewModels { factory }

        val loadingScreen = LoadingScreen(this, R.layout.loading_screen)
        loadingScreen.start()

        viewModel.getAllPhotosOfProperty(propertyId, {
            for(i in 0 until it.size) {
                adapterImage(i)?.setImageBitmap(it[i])
                adapterImage(i)?.visibility = View.VISIBLE
            }
            for(i in it.size..19)
                adapterImage(i)?.visibility = View.GONE

            viewModel.getPropertyOwnersInformationById(propertyId, { ownerData_ ->
                ownerData = ownerData_!!

                viewModel.getPropertyInformationById(propertyId, { propertyData_ ->
                    propertyData = propertyData_!!

                    handleOwnerData()
                    handlePropertyData()
                    allowDeletion()

                    loadingScreen.end()
                    Log.d(TAG, "Successfully retrieved propriety data")
                }, {
                    loadingScreen.end()
                    Log.e(TAG, "Failed to retrieve propriety data")
                })

            }, {
                loadingScreen.end()
                Log.e(TAG, "Failed to retrieve ownerData data")
            })

            loadingScreen.end()
        }, {
            Log.e(TAG, "Failed to retrieve photos")
            loadingScreen.end()
        })

        initUI()

    }

    private fun initUI() {
        val factory = AuthenticationInjector.provideListingExpandedViewModelFactory()
        val viewModel: ListingExpandedViewModel by viewModels { factory }

        binding.toolbar.back.setOnClickListener { finish() }
        binding.phone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL);
            intent.data = Uri.parse("tel:${ownerData.phone}")
            startActivity(intent)
        }
        binding.delete.setOnClickListener {
            val loadingScreen = LoadingScreen(this, R.layout.loading_screen)
            loadingScreen.start()
            viewModel.deletePropertyById(propertyId, {
                Log.d(TAG, "successfully deleted property")
                loadingScreen.end()
                finish()
            }, {
                Log.w(TAG, "failed to delete property")
                loadingScreen.end()
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handlePropertyData() {
        binding.bedroomsNumber.text = if(propertyData.bedrooms == 6) "6+"
            else propertyData.bedrooms.toString()
        binding.bathroomsNumber.text = if(propertyData.bathrooms == 6) "6+"
            else propertyData.bathrooms.toString()
        binding.kitchensNumber.text = if(propertyData.kitchens == 3) "3+"
            else propertyData.kitchens.toString()

        binding.price.text = "${propertyData.price.toString()} $" +
                if(propertyData.listingType == "For Rent") "/month" else ""
        binding.surface.text = propertyData.surface.toString() + " sqm"
        binding.propertyType.text = propertyData.propertyType
        binding.listingType.text = propertyData.listingType

        binding.propertyDescription.text = propertyData.description
        binding.amenitiesList.text = test(propertyData.amenities!!)
    }

    private fun handleOwnerData() {
        val factory = AuthenticationInjector.provideListingExpandedViewModelFactory()
        val viewModel: ListingExpandedViewModel by viewModels { factory }

        binding.ownerName.text = ownerData.name
        binding.ownerEmail.text = ownerData.email
    }

    private fun test(amenities: Map<String, Int>): String {
        val result = StringBuilder()
        amenities.forEach {
            if(it.key == "Parking spaces")
                result.append("${it.value} ${it.key}")
            else
                when(it.value) {
                    1 -> result.append(it.key + ", ")
                    else -> result.append("")
                }
        }
        return result.toString().dropLast(2)
    }

    private fun allowDeletion() {
        binding.delete.visibility =
            if(ownerData.email == FirebaseAuth.getInstance().currentUser?.email)
                View.VISIBLE
            else
                View.GONE
    }

    private fun adapterImage(pos: Int): ImageView? = when(pos) {
            0 -> binding.listImage0
            1 -> binding.listImage1
            2 -> binding.listImage2
            3 -> binding.listImage3
            4 -> binding.listImage4
            5 -> binding.listImage5
            6 -> binding.listImage6
            7 -> binding.listImage7
            8 -> binding.listImage8
            9 -> binding.listImage9
            10 -> binding.listImage10
            11 -> binding.listImage11
            12 -> binding.listImage12
            13 -> binding.listImage13
            14 -> binding.listImage14
            15 -> binding.listImage15
            16 -> binding.listImage16
            17 -> binding.listImage17
            18 -> binding.listImage18
            19 -> binding.listImage19
            else -> null
        }

}








































