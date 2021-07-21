package com.example.hearmeout.playback

import android.media.MediaMetadata
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import com.example.hearmeout.data.Song
import java.lang.Exception

//Utility method references - may delete if not required
fun getMediaItems(mediaList : List<MediaMetadataCompat>) : MutableList<MediaBrowserCompat.MediaItem> {
    val mediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()
    mediaList.forEach {
        mediaItems.add(
            MediaBrowserCompat.MediaItem(
                it.description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE))
    }
    return mediaItems
}

fun convertToMediaMetadata(songs : List<Song>) : List<MediaMetadataCompat> {
    val mediaList = mutableListOf<MediaMetadataCompat>()
    songs.forEach { song ->
        val mediaMetadata = MediaMetadataCompat.Builder().run {
            putString(MediaMetadata.METADATA_KEY_MEDIA_ID, song.source.hashCode().toString())
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
        }
        mediaList.add(mediaMetadata)
    }
    return mediaList
}

fun convertToSongs(mediaItems : List<MediaBrowserCompat.MediaItem>) : List<Song> {
    Log.i("Aarathi", "convertToSongs - ${mediaItems.size}")
    val songs = mutableListOf<Song>()
    mediaItems.forEach { mediaItem ->
        try {
            val data = mediaItem.description
            songs.add(
                Song(
                    data.title.toString(),
                    data.description.toString(),
                    data.subtitle.toString(),"",
                    data.mediaUri.toString(),
                    data.iconUri.toString(), 0,0,0)
            )
        } catch (e : Exception) {
            Log.i("Aarathi", "Null pointer")
        }
    }
    return songs
}