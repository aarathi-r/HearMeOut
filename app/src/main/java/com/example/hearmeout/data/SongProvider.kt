package com.example.hearmeout.data

import android.util.Log
import com.example.hearmeout.util.BASE_URL
import com.example.hearmeout.util.MUSIC_ENDPOINT
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory

class SongProvider {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val networkInterface: NetworkInterface by lazy {
        retrofit.create(NetworkInterface::class.java)
    }

    suspend fun fetchSongs() : List<Song> {
        Log.i("Aarathi", "Fetch the Songs")
        try {
            val music = fetchSongsFromNetwork().await()
            music.songs.forEach { song ->
                song.image = BASE_URL + MUSIC_ENDPOINT + song.image
                song.source = BASE_URL + MUSIC_ENDPOINT + song.source
            }
            return music.songs
        } catch (t : Throwable) {

        }
        return emptyList()
    }

    private fun fetchSongsFromNetwork() : Call<Music> {
        return networkInterface.getAllSongs()
    }
}