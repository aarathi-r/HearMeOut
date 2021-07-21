package com.example.hearmeout.ui

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log

fun handlePlayPause(mediaController : MediaControllerCompat, media : MediaBrowserCompat.MediaItem) {
    val playbackState = mediaController.playbackState.state
    Log.i("Aarathi", "Playback state - $playbackState")

    when (playbackState) {
        PlaybackStateCompat.STATE_PLAYING -> mediaController.transportControls.pause()
        PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.STATE_NONE -> mediaController.transportControls.playFromMediaId(media.description.mediaUri.toString(), null)
        else -> Log.i("Aarathi", "Not handled - state: $playbackState")
    }
}

