package com.example.hearmeout.playback

import android.media.MediaMetadata
import android.media.MediaPlayer
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log

class PlaybackActions(private val player : MediaPlayer) {

    private val queueManager = PlaybackQueueManager()

    fun play(mediaId : String?, curPos : Int) {
        mediaId?.let { queueManager.updatePlayingQueue(mediaId)}
        val mediaUrl = queueManager.getCurrentQueueItem()
            .description.mediaUri.toString()
        player.apply {
            reset()
            setDataSource(mediaUrl)
            prepare()
            seekTo(curPos)
        }
    }

    fun pause() {

    }

    fun stop() {

    }

    fun skipToPrevious() {
        queueManager.updatePlayingIndex(-1)
        play(null, 0)
    }

    fun skipToNext() {
        queueManager.updatePlayingIndex(1)
        play(null, 0)
    }


    fun getPlaybackState(state : Int) : PlaybackStateCompat {
        return PlaybackStateCompat.Builder().run {
            setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1.0F)
            build()
        }
    }

    fun getMetadata() : MediaMetadataCompat {
        val media = queueManager.getCurrentQueueItem().description
        val duration = media.extras?.get(MediaMetadata.METADATA_KEY_DURATION) as Long
        return MediaMetadataCompat.Builder().run {
            putString(MediaMetadata.METADATA_KEY_MEDIA_ID, media.mediaId)
            putString(MediaMetadata.METADATA_KEY_TITLE, media.title.toString())
            putString(MediaMetadata.METADATA_KEY_ARTIST, media.subtitle.toString())
            putString(MediaMetadata.METADATA_KEY_ALBUM, media.description.toString())
            putString(MediaMetadata.METADATA_KEY_MEDIA_URI, media.mediaUri.toString())
            putString(MediaMetadata.METADATA_KEY_ART_URI, media.iconUri.toString())
            putLong(MediaMetadata.METADATA_KEY_DURATION, duration)
            build()
        }
    }
}