package com.example.hearmeout.playback

import android.media.MediaPlayer

class PlaybackActions(private val player : MediaPlayer) {

    fun play(mediaId : String, curPos : Int) {
        player.apply {
            reset()
            setDataSource(mediaId)
            prepare()
            seekTo(curPos)
        }
    }

    fun pause() {

    }

    fun stop() {

    }

    fun skipToPrevious() {

    }

    fun skipToNext() {

    }
}