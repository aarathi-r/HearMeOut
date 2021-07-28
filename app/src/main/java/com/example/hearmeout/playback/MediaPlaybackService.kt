package com.example.hearmeout.playback

import android.app.Application
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.example.hearmeout.data.SongProvider
import com.example.hearmeout.util.CATEGORY_ALBUM
import com.example.hearmeout.util.CATEGORY_ARTIST
import com.example.hearmeout.util.CATEGORY_GENRE
import kotlinx.coroutines.*

private const val LOG_TAG = "MediaPlayerService"
private const val EMPTY_MEDIA_ROOT_ID = "empty_root_id"
private const val MEDIA_ROOT_ID = "root_id"
private const val MEDIA_ROOT_ALBUM = "root_id_album"
private const val MEDIA_ROOT_ARTIST = "root_id_artist"
private const val MEDIA_ROOT_GENRE = "root_id_genre"


class MediaPlaybackService : MediaBrowserServiceCompat() {

    private var mediaSession : MediaSessionCompat? = null
    private lateinit var playbackStateBuilder : PlaybackStateCompat.Builder
    private lateinit var mediaMetadataBuilder : MediaMetadataCompat.Builder

    private lateinit var songs : List<MediaMetadataCompat>

    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(coroutineJob + Dispatchers.Main)

    private lateinit var sessionCallbacks : MediaSessionCallbacks

    override fun onCreate() {
        super.onCreate()
        Log.i("Aarathi", "MediaPlaybackService - onCreate")
        playbackStateBuilder = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE)

        sessionCallbacks = MediaSessionCallbacks(this)

        mediaSession = MediaSessionCompat(baseContext, LOG_TAG).apply {
            setCallback(sessionCallbacks)
            setPlaybackState(playbackStateBuilder.build())
            setSessionToken(sessionToken)
        }

        fetchSongsFromSource()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return if(clientUid == android.os.Process.myUid()
            && clientPackageName == Application.getProcessName())
        {
            BrowserRoot(MEDIA_ROOT_ID, null)
        } else {
            BrowserRoot(EMPTY_MEDIA_ROOT_ID, null)
        }
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        var mediaItems : MutableList<MediaBrowserCompat.MediaItem>
        when(parentId) {
            EMPTY_MEDIA_ROOT_ID -> {
                result.sendResult(mutableListOf())
            }
            else -> {
                result.detach()
                coroutineScope.launch {
                    mediaItems = getSongsForDisplay(parentId)
                    //TODO temporary - need to change to some other place
                    sessionCallbacks.initMediaList(mediaItems)
//                    val queue = withContext(Dispatchers.Default) {
//                        getQueueItem(mediaItems)
//                    }
//                    mediaSession?.setQueue(queue)
                    result.sendResult(mediaItems)
                }
            }
        }

    }

    override fun onLoadItem(itemId: String, result: Result<MediaBrowserCompat.MediaItem>) {
        val metadata = SongProvider.getMetadataForMediaId(itemId)
        val mediaItem = metadata?.let { getAllMediaItems(listOf(it))}
        return result.sendResult(mediaItem?.get(0))
    }

    fun getMediaSession() : MediaSessionCompat? {
        return mediaSession
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineJob.cancel()
        mediaSession?.release()
    }

    private fun fetchSongsFromSource() {
        Log.i("Aarathi", "Fetch the Songs")
        coroutineScope.launch {
            try {
                songs = SongProvider.fetchSongs()
                Log.i("Aarathi", "Songs : ${songs.size}")
            } catch (t : Throwable) {

            }
        }
    }

    private suspend fun getSongsForDisplay(mediaRoot : String) : MutableList<MediaBrowserCompat.MediaItem> {
        return withContext(Dispatchers.Default) {
            do {
                delay(100)
            } while(!this@MediaPlaybackService::songs.isInitialized)
            when(mediaRoot) {
                MEDIA_ROOT_ID -> getAllMediaItems(songs)
                MEDIA_ROOT_ALBUM -> getCategoryMediaItems(songs, CATEGORY_ALBUM)
                MEDIA_ROOT_ARTIST -> getCategoryMediaItems(songs, CATEGORY_ARTIST)
                MEDIA_ROOT_GENRE -> getCategoryMediaItems(songs, CATEGORY_GENRE)
                else -> mutableListOf()
            }
        }
    }
}