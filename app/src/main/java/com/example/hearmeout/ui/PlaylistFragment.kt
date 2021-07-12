package com.example.hearmeout.ui

import android.content.ComponentName
import android.media.MediaMetadata
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.hearmeout.R
import com.example.hearmeout.databinding.FragmentPlaylistBinding
import com.example.hearmeout.playback.MediaControllerCallbacks
import com.example.hearmeout.playback.MediaPlaybackService
import com.example.hearmeout.viewmodel.PlaylistViewModel

class PlaylistFragment : Fragment() {

    private lateinit var mediaBrowser: MediaBrowserCompat
    private lateinit var binding : FragmentPlaylistBinding
    private lateinit var controllerCallbacks : MediaControllerCallbacks
    private val playlistViewModel : PlaylistViewModel by viewModels()

    private val connCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            Log.i("Aarathi","MediaBrowser connected to the service")
            val mediaController = MediaControllerCompat(this@PlaylistFragment.requireActivity(), mediaBrowser.sessionToken)
            MediaControllerCompat.setMediaController(this@PlaylistFragment.requireActivity(), mediaController)
            buildUI()
        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mediaBrowser = MediaBrowserCompat(activity,
            ComponentName(this.requireActivity(), MediaPlaybackService::class.java),
            connCallback,
            null)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false)
        binding.lifecycleOwner = this
        binding.
        controllerCallbacks = MediaControllerCallbacks(binding)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser.connect()
    }

    override fun onStop() {
        super.onStop()
        mediaBrowser.disconnect()
        MediaControllerCompat.getMediaController(this.requireActivity())?.unregisterCallback(controllerCallbacks)
    }

    private fun buildUI() {
        Log.i("Aarathi", "buildUI called")
        MediaControllerCompat.getMediaController(this.requireActivity()).registerCallback(controllerCallbacks)
        binding.playPause.setOnClickListener {
            playAudio()
        }
    }

    fun playAudio() {
        Log.i("Aarathi", "Play button clicked")
        if (mediaBrowser.isConnected) {
            val mediaController = MediaControllerCompat.getMediaController(this.requireActivity())
            val playbackState = mediaController.playbackState.state
            Log.i("Aarathi", "Playback state - $playbackState")
            when(playbackState) {
                PlaybackStateCompat.STATE_PLAYING -> mediaController.transportControls.pause()
                PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.STATE_NONE -> mediaController.transportControls.play()
                else -> Log.i("Aarathi","Not handled - state: $playbackState")
            }
        }
    }

}