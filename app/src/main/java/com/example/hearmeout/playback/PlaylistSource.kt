package com.example.hearmeout.playback

import android.media.MediaMetadata
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.example.hearmeout.data.Song
import com.example.hearmeout.util.CATEGORY_ALBUM
import com.example.hearmeout.util.CATEGORY_ARTIST
import com.example.hearmeout.util.CATEGORY_GENRE

fun getAllMediaItems(songs : List<Song>) : MutableList<MediaBrowserCompat.MediaItem> {
    val mediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()
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
        mediaItems.add(MediaBrowserCompat.MediaItem(
            mediaMetadata.description,
            MediaBrowserCompat.MediaItem.FLAG_PLAYABLE))
    }
    return mediaItems
}

fun getCategoryMediaItems(allSongs : List<Song>, category : String) : MutableList<MediaBrowserCompat.MediaItem> {
    val songsByCategory = when(category) {
        CATEGORY_ALBUM -> getSongsByCategory(allSongs, CATEGORY_ALBUM)
        CATEGORY_ARTIST -> getSongsByCategory(allSongs, CATEGORY_ARTIST)
        CATEGORY_GENRE -> getSongsByCategory(allSongs, CATEGORY_GENRE)
        else -> emptyMap()
    }
    val mediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()
    songsByCategory.forEach { entry ->
        val key = entry.key
        val songs = entry.value
        val mediaDescription = MediaDescriptionCompat.Builder().run {
            setMediaId(key.hashCode().toString())
            setTitle(key)
            setSubtitle("${songs.size} Songs")
            setIconUri(songs[0].image.toUri())
            build()
        }
        mediaItems.add(MediaBrowserCompat.MediaItem(mediaDescription, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE))
    }
    return mediaItems
}

private fun getSongsByCategory(allSongs : List<Song>, category : String) : HashMap<String, MutableList<Song>> {
    val songsByCategory = HashMap<String, MutableList<Song>>()
    allSongs.forEach {
        val categoryValue = when(category) {
            CATEGORY_ALBUM -> it.album
            CATEGORY_ARTIST -> it.artist
            CATEGORY_GENRE -> it.genre
            else -> it.artist
        }
        val songs = songsByCategory[categoryValue]?.apply { add(it) } ?: mutableListOf(it)
        songsByCategory[categoryValue] = songs
    }
    return songsByCategory
}