
package com.example.hearmeout.data

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import com.example.hearmeout.playback.*
import com.example.hearmeout.util.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory

class SongProvider {

    private lateinit var songs : List<Song>
    private var mediaList = mutableListOf<MediaMetadataCompat>()
    private var mediaIdToMetadataMap = HashMap<String, MediaMetadataCompat>()

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
        return withContext(Dispatchers.IO) {
            val music = fetchSongsFromNetwork()
            music.songs.forEach { song ->
                song.image = BASE_URL + MUSIC_ENDPOINT + song.image
                song.source = BASE_URL + MUSIC_ENDPOINT + song.source
                val mediaId = song.source.hashCode().toString()
                val metadata = convertToMetadata(song, mediaId)
                mediaIdToMetadataMap[mediaId] = metadata
            }
            music.songs
        }
    }

    private suspend fun fetchSongsFromNetwork() : Music {
        return networkInterface.getAllSongs()
    }

    fun getMetadataForMediaId(mediaId: String): MediaMetadataCompat? {
        return mediaIdToMetadataMap[mediaId]
    }
}