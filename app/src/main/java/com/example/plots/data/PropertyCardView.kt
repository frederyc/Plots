package com.example.plots.data

import android.graphics.Bitmap

data class PropertyCardView(
    val propertyId: String,
    val imageResource: Bitmap?,
    val listingType: String,
    val price: Int,
    val surface: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val kitchens: Int)