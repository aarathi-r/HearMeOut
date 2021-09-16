package com.example.hearmeout.playback

import android.support.v4.media.session.MediaSessionCompat
import com.example.hearmeout.data.PlaylistProvider

class PlaybackQueueManager {
    private var playingQueue = listOf<MediaSessionCompat.QueueItem>()
    private var playingQueueTitle : String? = null
    private var playingIndex = 0

    fun updatePlayingQueue(mediaId : String) {
        val queueInfo = mediaId.split(":", ignoreCase = true, limit = 3)
        queueInfo.takeIf { 3 == it.size }
            ?.run {
                val queueTitle = this[0]
                val index = this[1]
                val id = this[2]

                if (null == playingQueueTitle || queueTitle != playingQueueTitle) {
                    playingQueueTitle = queueTitle
                    playingQueue = PlaylistProvider.getQueue(queueTitle)
                    playingIndex = index.toInt()

                } else if (queueTitle == playingQueueTitle) {
                    playingIndex = index.toInt()
                }
            }
    }

    fun updatePlayingIndex(offset : Int) {
        playingIndex += offset
    }

    fun getCurrentQueueItem() : MediaSessionCompat.QueueItem{
        return playingQueue[playingIndex]
    }
}