package com.example.hearmeout.data

import com.example.hearmeout.util.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class SongProvider {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val networkInterface : NetworkInterface by lazy {
        retrofit.create(NetworkInterface::class.java)
    }

    fun fetchSongsFromNetwork() {
        networkInterface.getAllSongs().enqueue(object: Callback<Music> {
            override fun onResponse(call: Call<Music>, response: Response<Music>) {

            }

            override fun onFailure(call: Call<Music>, t: Throwable) {

            }
        })
    }
}