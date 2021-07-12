package com.example.hearmeout.data

import retrofit2.Call
import retrofit2.http.GET

interface NetworkInterface {
    @GET("automotive-media/music.json")
    fun getAllSongs() : Call<Music>
}