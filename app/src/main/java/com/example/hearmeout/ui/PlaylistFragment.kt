package com.example.hearmeout.ui

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
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
import com.example.hearmeout.ui.adapters.PlaylistAdapter
import com.example.hearmeout.ui.adapters.MediaClickListener
import com.example.hearmeout.viewmodel.PlaylistViewModel

class PlaylistFragment : Fragment() {

    private lateinit var mediaBrowser: MediaBrowserCompat
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var controllerCallbacks: MediaControllerCallbacks
    private var mediaRoot : String? = null

    private val playlistAdapter: PlaylistAdapter by lazy {
        PlaylistAdapter(MediaClickListener { media ->
            if (media.isPlayable)
                play(media)
            else if (media.isBrowsable)
                browse(media)
        })
    }

    private val playlistViewModel: PlaylistViewModel by viewModels()

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            playlistViewModel.refreshMediaItems(children)
        }

        override fun onError(parentId: String) {
            super.onError(parentId)
        }
    }

    private val connCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            Log.i("Aarathi", "MediaBrowser connected to the service")
            val mediaController = MediaControllerCompat(
                this@PlaylistFragment.requireActivity(),
                mediaBrowser.sessionToken)

            MediaControllerCompat.setMediaController(
                this@PlaylistFragment.requireActivity(),
                mediaController)

            mediaRoot = mediaBrowser.root
            mediaRoot?.let {
                mediaBrowser.subscribe(it, subscriptionCallback)
            }
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
        Log.i("Aarathi", "onCreateView - PlaylistFragment")
        mediaBrowser = MediaBrowserCompat(
            activity,
            ComponentName(this.requireActivity(), MediaPlaybackService::class.java),
            connCallback,
            null
        )

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = playlistViewModel
        binding.playList.adapter = playlistAdapter
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
        MediaControllerCompat.getMediaController(this.requireActivity())
            ?.unregisterCallback(controllerCallbacks)
        mediaRoot?.let {
            mediaBrowser.unsubscribe(it, subscriptionCallback)
        }
    }

    private fun buildUI() {
        Log.i("Aarathi", "buildUI called")
        MediaControllerCompat.getMediaController(this.requireActivity())
            .registerCallback(controllerCallbacks)
    }

    private fun play(mediaItem : MediaBrowserCompat.MediaItem) {
        Log.i("Aarathi", "Play button clicked")
        if (mediaBrowser.isConnected) {
            handlePlayPause(MediaControllerCompat.getMediaController(this.requireActivity()), mediaItem)
        }
    }

    private fun browse(mediaItem : MediaBrowserCompat.MediaItem) {
        Log.i("Aarathi", "Browse button clicked")
        if (mediaBrowser.isConnected) {
            //TODO
        }
    }
}