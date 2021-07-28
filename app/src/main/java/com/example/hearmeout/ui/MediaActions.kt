package com.example.hearmeout.ui

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log

fun updateMediaMetadata(mediaController : MediaControllerCompat, media : MediaDescriptionCompat) {
    mediaController.playbackState.state
        .takeIf { it == PlaybackStateCompat.STATE_NONE
                || it == PlaybackStateCompat.STATE_PLAYING
        }?.run { mediaController.transportControls
            .playFromMediaId(media.mediaUri.toString(), null)
        }
}

fun handlePlayCurrent(mediaController : MediaControllerCompat, media : MediaDescriptionCompat) {
    val playbackState = mediaController.playbackState.state
    Log.i("Aarathi", "Playback state - $playbackState")

    when (playbackState) {
        PlaybackStateCompat.STATE_PLAYING -> mediaController.transportControls.pause()
        PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.STATE_NONE -> mediaController.transportControls.playFromMediaId(media.mediaUri.toString(), null)
        else -> Log.i("Aarathi", "Not handled - state: $playbackState")
    }
}

fun handlePlayPrevious(mediaController : MediaControllerCompat) {
    mediaController.transportControls.skipToPrevious()
}

fun handlePlayNext(mediaController : MediaControllerCompat) {
    mediaController.transportControls.skipToNext()
}

