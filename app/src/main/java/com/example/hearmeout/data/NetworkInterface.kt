package com.example.hearmeout.data

import retrofit2.http.GET

interface NetworkInterface {
    @GET("automotive-media/music.json")
    suspend fun getAllSongs() : Music
}