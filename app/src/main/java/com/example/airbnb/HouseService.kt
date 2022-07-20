package com.example.airbnb

import com.example.airbnb.model.HouseDto
import com.example.airbnb.model.HouseModel
import retrofit2.Call
import retrofit2.http.GET

interface HouseService {

    @GET("/v3/c3e88fdb-18f1-4a80-b546-be68720859db")
    fun getHouseList(
    ) : Call<HouseDto>
}