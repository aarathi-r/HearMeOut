package com.example.hearmeout.playback

import android.media.MediaMetadata
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.net.toUri
import com.example.hearmeout.util.CATEGORY_ALBUM
import com.example.hearmeout.util.CATEGORY_ARTIST
import com.example.hearmeout.util.CATEGORY_GENRE

fun getAllMediaItems(mediaList : List<MediaMetadataCompat>) : MutableList<MediaBrowserCompat.MediaItem> {
    val mediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()
    mediaList.forEach { metadata ->
        mediaItems.add(MediaBrowserCompat.MediaItem(
            metadata.description,
            MediaBrowserCompat.MediaItem.FLAG_PLAYABLE))
    }
    return mediaItems
}

fun getCategoryMediaItems(allMedia : List<MediaMetadataCompat>, category : String) : MutableList<MediaBrowserCompat.MediaItem> {
    val songsByCategory = when(category) {
        CATEGORY_ALBUM -> getSongsByCategory(allMedia, MediaMetadata.METADATA_KEY_ALBUM)
        CATEGORY_ARTIST -> getSongsByCategory(allMedia, MediaMetadata.METADATA_KEY_ARTIST)
        CATEGORY_GENRE -> getSongsByCategory(allMedia, MediaMetadata.METADATA_KEY_GENRE)
        else -> emptyMap()
    }
    val mediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()
    songsByCategory.forEach { entry ->
        val key = entry.key
        val mediaList = entry.value
        val mediaDescription = MediaDescriptionCompat.Builder().run {
            setMediaId(key.hashCode().toString())
            setTitle(key)
            setSubtitle("${mediaList.size} Songs")
            setIconUri(mediaList[0].getString(MediaMetadata.METADATA_KEY_ART_URI).toUri())
            build()
        }
        mediaItems.add(MediaBrowserCompat.MediaItem(mediaDescription, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE))
    }
    return mediaItems
}

private fun getSongsByCategory(allMedia : List<MediaMetadataCompat>, categoryKey : String) : HashMap<String, MutableList<MediaMetadataCompat>> {
    val songsByCategory = HashMap<String, MutableList<MediaMetadataCompat>>()
    allMedia.forEach {
        val categoryValue = it.getString(categoryKey)
        val songs = songsByCategory[categoryValue]?.apply { add(it) } ?: mutableListOf(it)
        songsByCategory[categoryValue] = songs
    }
    return songsByCategory
}

fun getQueueItem(mediaItems : List<MediaBrowserCompat.MediaItem>) : List<MediaSessionCompat.QueueItem> {
    val queue = mutableListOf<MediaSessionCompat.QueueItem>()
    mediaItems.forEach {
        it.description.mediaId?.let { id  ->
            MediaSessionCompat.QueueItem(it.description, id.toLong())
        }?.let { queueItem ->
            queue.add(queueItem)
        }
    }
    return queue
}