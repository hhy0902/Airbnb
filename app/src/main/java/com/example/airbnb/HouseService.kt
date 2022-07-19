package com.example.airbnb

import com.example.airbnb.model.HouseModel
import retrofit2.Call
import retrofit2.http.GET

interface HouseService {

    @GET("/v3/f76e4040-abed-48c8-bfb6-5ecabc7ddb5a")
    fun getHouseList(

    ) : Call<HouseModel>
}