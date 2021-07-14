package com.example.hearmeout.playback

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat

private const val LOG_TAG = "MediaPlayerService"
private const val EMPTY_MEDIA_ROOT_ID = "empty_root_id"

class MediaPlaybackService : MediaBrowserServiceCompat() {

    private var mediaSession : MediaSessionCompat? = null
    private lateinit var playbackStateBuilder : PlaybackStateCompat.Builder
    private lateinit var mediaMetadataBuilder : MediaMetadataCompat.Builder

    override fun onCreate() {
        super.onCreate()

        playbackStateBuilder = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE)

        mediaSession = MediaSessionCompat(baseContext, LOG_TAG).apply {
            setCallback(MediaSessionCallbacks(this@MediaPlaybackService))
            setPlaybackState(playbackStateBuilder.build())
            setSessionToken(sessionToken)
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(EMPTY_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        if (EMPTY_MEDIA_ROOT_ID == parentId) {
            result.sendResult(mutableListOf())
        }
    }

    fun getMediaSession() : MediaSessionCompat? {
        return mediaSession
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession?.release()
    }
}