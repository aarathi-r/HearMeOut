package com.example.hearmeout.data

import android.media.MediaMetadata
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import com.example.hearmeout.util.BASE_URL
import com.example.hearmeout.util.MUSIC_ENDPOINT
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory

object SongProvider {

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

    suspend fun fetchSongs() : List<MediaMetadataCompat> {
        Log.i("Aarathi", "Fetch the Songs")

        try {
            withContext(Dispatchers.IO) {
                val music = fetchSongsFromNetwork()
                music.songs.forEach { song ->
                    //val mediaId = song.source.hashCode().toString()
                    val mediaId = song.source
                    song.image = BASE_URL + MUSIC_ENDPOINT + song.image
                    song.source = BASE_URL + MUSIC_ENDPOINT + song.source
                    MediaMetadataCompat.Builder().run {
                        putString(MediaMetadata.METADATA_KEY_MEDIA_ID, mediaId)
                        putString(MediaMetadata.METADATA_KEY_TITLE, song.title)
                        putString(MediaMetadata.METADATA_KEY_ALBUM, song.album)
                        putString(MediaMetadata.METADATA_KEY_ARTIST, song.artist)
                        putString(MediaMetadata.METADATA_KEY_GENRE, song.genre)
                        putString(MediaMetadata.METADATA_KEY_MEDIA_URI, song.source)
                        putString(MediaMetadata.METADATA_KEY_ART_URI, song.image)
                        putLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER, song.trackNumber)
                        putLong(MediaMetadata.METADATA_KEY_NUM_TRACKS, song.trackCount)
                        putLong(MediaMetadata.METADATA_KEY_DURATION, song.duration)
                        build()
                    }.let { metadata ->
                        mediaIdToMetadataMap[mediaId] = metadata
                        mediaList.add(metadata)
                    }
                }
            }
        } catch (t : Throwable) {

        }
        return mediaList
    }

    private suspend fun fetchSongsFromNetwork() : Music {
        return networkInterface.getAllSongs()
    }

    fun getMetadataForMediaId(mediaId : String) : MediaMetadataCompat? {
        val metadata = mediaIdToMetadataMap[mediaId]
        return metadata
    }
}