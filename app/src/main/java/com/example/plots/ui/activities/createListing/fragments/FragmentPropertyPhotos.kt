package com.example.plots.ui.activities.createListing.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.plots.R
import com.example.plots.databinding.FragmentPropertyPhotosBinding

class FragmentPropertyPhotos : Fragment(R.layout.fragment_property_description) {

    private val TAG = "FragmentPropertyPhotos"

    private lateinit var binding: FragmentPropertyPhotosBinding
    private lateinit var resultContract: ActivityResultLauncher<Intent>
    private var images: ArrayList<Uri?>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: started")
        Log.d(TAG, "onCreate: ended")

        resultContract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    if(it.data!!.clipData != null) {
                        // picked multiple images
                        images!!.clear()
                        val clipData = it.data!!.clipData!!
                        if(clipData.itemCount > 20)
                            Toast.makeText(context, "You can pick a maximum of 20 images",
                                Toast.LENGTH_LONG).show()
                        else
                            for(i in 0 until clipData.itemCount)
                                images!!.add(clipData.getItemAt(i).uri)
                    }
                    else {
                        // picked single image
                        images!!.clear()
                        images!!.add(it.data?.data)
                    }
                    updateImagesUploadedTextInfo()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: started")
        Log.d(TAG, "onCreateView: ended")
        return inflater.inflate(R.layout.fragment_property_photos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: started")

        binding = FragmentPropertyPhotosBinding.bind(view)
        if(images!!.isNotEmpty())
            updateImagesUploadedTextInfo()
        initUI()

        Log.d(TAG, "onViewCreated: ended")
    }

    // To be used by activity code
    fun getImageUris(): ArrayList<Uri?> = images!!

    private fun initUI() {
        binding.selectPhotos.setOnClickListener { pickImagesFromGallery() }
    }

    @SuppressLint("SetTextI18n")
    private fun updateImagesUploadedTextInfo() {
        binding.imagesUploadedTextInfo.text = "You have selected ${images!!.size} photos"
    }

    private fun pickImagesFromGallery() {
        Log.d(TAG, "pickImagesFromGallery: started")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        resultContract.launch(intent)
        Log.d(TAG, "pickImagesFromGallery: ended")
    }

}