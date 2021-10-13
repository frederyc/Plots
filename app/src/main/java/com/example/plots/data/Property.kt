package com.example.plots.data

data class Property(
    var ownerKey: String? = null,
    var photosCollectionKey: String? = null,
    var imageKeys: Map<String, String>? = null,
    var propertyType: String? = null,
    var amenities: Map<String, Int>? = null,
    var listingType: String? = null,
    var price: Int? = null,
    var surface: Int? = null,
    var bedrooms: Int? = null,
    var bathrooms: Int? = null,
    var kitchens: Int? = null,
    var description: String? = null
)