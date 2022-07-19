package com.example.airbnb.model


import com.google.gson.annotations.SerializedName

data class HouseModel(
    @SerializedName("items")
    val items: List<Item>?
)