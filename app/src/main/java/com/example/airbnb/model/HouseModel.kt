package com.example.airbnb.model


import com.google.gson.annotations.SerializedName

data class HouseModel(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("lat")
    val lat: Double?,
    @SerializedName("lon")
    val lon: Double?,
    @SerializedName("price")
    val price: String?,
    @SerializedName("title")
    val title: String?
)