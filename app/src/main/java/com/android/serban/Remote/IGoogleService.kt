package com.android.serban.Remote

import com.android.serban.model.MyPlaces
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface IGoogleService {
    @GET
    fun getNearbyPlaces(@Url url:String):Call<MyPlaces>
}