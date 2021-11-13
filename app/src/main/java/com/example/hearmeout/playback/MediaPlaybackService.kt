package com.example.hearmeout.playback

import android.app.Application
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.example.hearmeout.data.PlaylistProvider
import com.example.hearmeout.data.PlaylistProvider.EMPTY_MEDIA_ROOT_ID
import com.example.hearmeout.data.PlaylistProvider.MEDIA_ROOT_ID
import com.example.hearmeout.data.SongProvider
import kotlinx.coroutines.*

private const val LOG_TAG = "MediaPlayerService"

class MediaPlaybackService : MediaBrowserServiceCompat() {

    private var mediaSession : MediaSessionCompat? = null
    private lateinit var playbackStateBuilder : PlaybackStateCompat.Builder
    private lateinit var mediaMetadataBuilder : MediaMetadataCompat.Builder

    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(coroutineJob + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        Log.i("Aarathi", "MediaPlaybackService - onCreate")
        playbackStateBuilder = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE)

        mediaSession = MediaSessionCompat(baseContext, LOG_TAG).apply {
            setCallback(MediaSessionCallbacks(this@MediaPlaybackService, this))
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
                    mediaItems = PlaylistProvider.getSongsForDisplay(parentId)
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
        //TODO update the logic later
        val metadata = SongProvider().getMetadataForMediaId(itemId)
        val mediaItem = metadata?.let { getAllMediaItems(listOf(it))}
        return result.sendResult(mediaItem?.get(0))
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Aarathi","MediaPlaybackService - onDestroy")
        coroutineJob.cancel()
        mediaSession?.release()
    }

    private fun fetchSongsFromSource() {
        coroutineScope.launch {
            try {
                PlaylistProvider.fetchSongs()
            } catch (t : Throwable) {
            }
        }
    }
}