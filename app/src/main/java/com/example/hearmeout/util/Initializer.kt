package com.example.hearmeout.util

import com.example.hearmeout.data.NetworkInterface
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

val retrofit : Retrofit by lazy {
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
}

val networkInterface : NetworkInterface by lazy {
    retrofit.create(NetworkInterface::class.java)
}