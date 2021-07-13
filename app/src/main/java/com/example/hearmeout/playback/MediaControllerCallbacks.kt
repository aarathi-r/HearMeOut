package com.example.hearmeout.playback

import android.media.MediaMetadata
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.example.hearmeout.R
import com.example.hearmeout.databinding.FragmentPlaylistBinding

class MediaControllerCallbacks(private val binding : FragmentPlaylistBinding) : MediaControllerCompat.Callback() {

    override fun onSessionReady() {
    }

    override fun onSessionDestroyed() {
    }

    override fun onSessionEvent(event: String?, extras: Bundle?) {
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        Log.i("Aarathi","Playback state is changed to ${state?.state}")
//        when(state?.state) {
//            PlaybackStateCompat.STATE_PLAYING ->  binding.playPause.setImageResource(R.drawable.pause_icon)
//            PlaybackStateCompat.STATE_PAUSED -> binding.playPause.setImageResource(R.drawable.play_icon)
//        }
    }

    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        Log.i("Aarathi","Metadata is changed")
        //binding.mediaData.text = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "No Title"
    }

    override fun onAudioInfoChanged(info: MediaControllerCompat.PlaybackInfo?) {
    }
}