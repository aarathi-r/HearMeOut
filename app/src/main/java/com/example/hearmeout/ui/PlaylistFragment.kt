package com.example.hearmeout.ui

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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.hearmeout.R
import com.example.hearmeout.databinding.FragmentPlaylistBinding
import com.example.hearmeout.ui.adapters.PlaylistAdapter
import com.example.hearmeout.ui.adapters.MediaClickListener
import com.example.hearmeout.viewmodel.PlaylistViewModel

class PlaylistFragment : Fragment() {

    private lateinit var binding : FragmentPlaylistBinding

    private lateinit var mediaBrowser : MediaBrowserCompat
    private var mediaRoot : String? = null

    private val playlistAdapter : PlaylistAdapter by lazy {
        PlaylistAdapter(MediaClickListener { media ->
            if (media.isPlayable)
                play(media)
            else if (media.isBrowsable)
                browse(media)
        })
    }

    private val playlistViewModel : PlaylistViewModel by viewModels()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Aarathi", "PlaylistFragment - onCreate")
        val args : PlaylistFragmentArgs by navArgs()
        this.mediaRoot = args.mediaRoot
        this.mediaBrowser = (requireActivity() as MainActivity).getMediaBrowser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = playlistViewModel
        binding.playList.adapter = playlistAdapter
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Log.i("Aarathi", "PlaylistFragment - onStart")
        if (mediaBrowser.isConnected) {
            Log.i("Aarathi", "PlaylistFragment - MediaBrowser connected")
            onServiceConnected()
        }
    }

    override fun onStop() {
        super.onStop()
        mediaRoot?.let {
            mediaBrowser.unsubscribe(it, subscriptionCallback)
        }
    }

    private fun onServiceConnected() {
        mediaRoot?.let {
            Log.i("Aarathi", "Subscribe for MediaItems")
            mediaBrowser.subscribe(it, subscriptionCallback)
        }

    }

    private fun play(mediaItem : MediaBrowserCompat.MediaItem) {
        Log.i("Aarathi", "Play button clicked")
        mediaItem.mediaId?.let {
            findNavController().navigate(PlaylistFragmentDirections.actionPlaylistFragmentToNowPlayingFragment(it))
        }
    }

    private fun browse(mediaItem : MediaBrowserCompat.MediaItem) {
        Log.i("Aarathi", "Browse button clicked")
        if (mediaBrowser.isConnected) {
            //TODO
        }
    }
}