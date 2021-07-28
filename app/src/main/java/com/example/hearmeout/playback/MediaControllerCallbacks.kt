package com.example.hearmeout.playback

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.example.hearmeout.R
import com.example.hearmeout.databinding.FragmentNowPlayingBinding

class MediaControllerCallbacks(private val binding : FragmentNowPlayingBinding) : MediaControllerCompat.Callback() {

    override fun onSessionReady() {
    }

    override fun onSessionDestroyed() {
    }

    override fun onSessionEvent(event: String?, extras: Bundle?) {
    }

    override fun onPlaybackStateChanged(playbackState: PlaybackStateCompat?) {
        when(playbackState?.state) {
            PlaybackStateCompat.STATE_PLAYING ->  binding.playCurrent.setImageResource(R.drawable.pause_icon)
            PlaybackStateCompat.STATE_PAUSED -> binding.playCurrent.setImageResource(R.drawable.play_icon)
            else -> Log.i("Aarathi", "Playback state ${playbackState?.state} not handled")
        }
    }

    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        Log.i("Aarathi","Metadata is changed")
        //binding.mediaData.text = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "No Title"
    }

    override fun onAudioInfoChanged(info: MediaControllerCompat.PlaybackInfo?) {
    }

    override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
        super.onQueueChanged(queue)
    }


}