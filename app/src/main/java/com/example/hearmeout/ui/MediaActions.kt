package com.example.hearmeout.ui

import android.app.Activity
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log

fun updateMediaMetadata(context : Activity, mediaId : String) {
    val mediaController = MediaControllerCompat.getMediaController(context)
    mediaController.playbackState.state
        .takeIf { it == PlaybackStateCompat.STATE_NONE
                || it == PlaybackStateCompat.STATE_PLAYING
        }?.run { mediaController.transportControls
            .playFromMediaId(mediaId, null)
        }
}

fun handlePlayCurrent(context : Activity, mediaId : String) {
    val mediaController = MediaControllerCompat.getMediaController(context)
    val playbackState = mediaController.playbackState.state
    Log.i("Aarathi", "Playback state - $playbackState")

    when (playbackState) {
        PlaybackStateCompat.STATE_PLAYING -> mediaController.transportControls.pause()
        PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.STATE_NONE -> mediaController.transportControls.playFromMediaId(mediaId, null)
        else -> Log.i("Aarathi", "Not handled - state: $playbackState")
    }
}

fun handlePlayPrevious(context : Activity) {
    val mediaController = MediaControllerCompat.getMediaController(context)
    mediaController.transportControls.skipToPrevious()
}

fun handlePlayNext(context : Activity) {
    val mediaController = MediaControllerCompat.getMediaController(context)
    mediaController.transportControls.skipToNext()
}

