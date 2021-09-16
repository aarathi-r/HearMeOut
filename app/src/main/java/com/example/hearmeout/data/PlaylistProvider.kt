package com.example.hearmeout.data

import android.media.MediaMetadata
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.net.toUri
import com.example.hearmeout.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

object PlaylistProvider {

    const val EMPTY_MEDIA_ROOT_ID = "empty_root_id"
    const val MEDIA_ROOT_ID = "root_id"
    const val MEDIA_ROOT_ALBUM = "root_id_album"
    const val MEDIA_ROOT_ARTIST = "root_id_artist"
    const val MEDIA_ROOT_GENRE = "root_id_genre"

    private lateinit var songs : List<Song>
    private val songProvider = SongProvider()

    private val allMediaQueue by lazy {
        mutableListOf<MediaSessionCompat.QueueItem>()
    }

    suspend fun fetchSongs() {
        songs = songProvider.fetchSongs()
    }

    suspend fun getSongsForDisplay(mediaRoot : String) : MutableList<MediaBrowserCompat.MediaItem> {
        return withContext(Dispatchers.Default) {
            do {
                delay(100)
            } while(!this@PlaylistProvider::songs.isInitialized)
            when(mediaRoot) {
                MEDIA_ROOT_ID -> getAllMediaItems(songs)
                MEDIA_ROOT_ALBUM -> getCategoryMediaItems(songs, CATEGORY_ALBUM)
                MEDIA_ROOT_ARTIST -> getCategoryMediaItems(songs, CATEGORY_ARTIST)
                MEDIA_ROOT_GENRE -> getCategoryMediaItems(songs, CATEGORY_GENRE)
                else -> mutableListOf()
            }
        }
    }

    private fun getAllMediaItems(songs : List<Song>) : MutableList<MediaBrowserCompat.MediaItem> {
        val mediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()
        allMediaQueue.clear()
        songs.forEach { song ->
            val mediaId = QUEUE_ALL.plus(":")
                .plus(mediaItems.size).plus(":")
                .plus(song.source.hashCode())
//            val metadata = convertToMetadata(song, mediaId)
//            mediaItems.add(
//                MediaBrowserCompat.MediaItem(
//                    metadata.description,
//                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE))
//            allMediaQueue.add(
//                MediaSessionCompat.QueueItem(
//                    metadata.description,
//                    song.source.hashCode().toLong()))
            val description = getDescription(mediaId, song)
            mediaItems.add(
                MediaBrowserCompat.MediaItem(
                    description,
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE))
            allMediaQueue.add(
                MediaSessionCompat.QueueItem(
                    description,
                    song.source.hashCode().toLong()))

        }
        return mediaItems
    }

    private fun getCategoryMediaItems(songs : List<Song>, category : String) : MutableList<MediaBrowserCompat.MediaItem> {
        val songsByCategory = when(category) {
            CATEGORY_ALBUM -> getSongsByCategory(songs, QUEUE_ALBUM)
            CATEGORY_ARTIST -> getSongsByCategory(songs, QUEUE_ARTIST)
            CATEGORY_GENRE -> getSongsByCategory(songs, QUEUE_GENRE)
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

    fun getQueue(queueTitle : String) : List<MediaSessionCompat.QueueItem> {
        return when (queueTitle) {
            QUEUE_ALL -> allMediaQueue
            else -> emptyList()
        }
    }
}

private fun getSongsByCategory(songs : List<Song>, queueCategory : String) : HashMap<String, MutableList<MediaMetadataCompat>> {
    val mediaByCategory = HashMap<String, MutableList<MediaMetadataCompat>>()
    songs.forEach { song ->
        val categoryValue = when(queueCategory) {
            QUEUE_ALBUM -> song.album
            QUEUE_ARTIST -> song.artist
            QUEUE_GENRE -> song.genre
            else -> song.genre
        }
        val mediaId = queueCategory.plus(categoryValue).plus(":")
            .plus(mediaByCategory[categoryValue]?.size).plus(":")
            .plus(song.source.hashCode())
        val metadata = convertToMetadata(song, mediaId)
        mediaByCategory[categoryValue] =
            mediaByCategory[categoryValue]?.apply { add(metadata) }
                ?: mutableListOf(metadata)
    }
    return mediaByCategory
}

fun convertToMetadata(song : Song, id : String) : MediaMetadataCompat {
    return MediaMetadataCompat.Builder().run {
        putString(MediaMetadata.METADATA_KEY_MEDIA_ID, id)
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
}

fun getDescription(mediaId : String, song : Song) : MediaDescriptionCompat {
    val extras = Bundle().apply {
        putLong(MediaMetadata.METADATA_KEY_DURATION, song.duration)
    }
    return MediaDescriptionCompat.Builder().run {
        setMediaId(mediaId)
        setTitle(song.title)
        setSubtitle(song.artist)
        setDescription(song.album)
        setIconBitmap(null)
        setIconUri(Uri.parse(song.image))
        setMediaUri(Uri.parse(song.source))
        setExtras(extras)
        build()
    }
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