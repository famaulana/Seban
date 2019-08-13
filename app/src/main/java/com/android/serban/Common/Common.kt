package com.android.serban.Common

import com.android.serban.Remote.IGoogleService
import com.android.serban.Remote.RetrofitClient
import retrofit2.Retrofit

object Common {
    private val GOOGLE_API_URL = "https://maps.googleapis.com"

    val googleApiService: IGoogleService
    get() = RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleService::class.java)
}