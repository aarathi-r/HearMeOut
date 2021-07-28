package com.example.hearmeout.ui

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.hearmeout.R
import com.example.hearmeout.databinding.FragmentNowPlayingBinding
import com.example.hearmeout.playback.MediaControllerCallbacks
import com.example.hearmeout.util.ACTION_PLAY_CURRENT
import com.example.hearmeout.util.ACTION_PLAY_NEXT
import com.example.hearmeout.util.ACTION_PLAY_PREVIOUS
import com.example.hearmeout.viewmodel.NowPlayingViewModel
import com.example.hearmeout.viewmodel.factory.NowPlayingViewModelFactory

class NowPlayingFragment : Fragment() {

    private lateinit var binding : FragmentNowPlayingBinding

    private lateinit var mediaBrowser : MediaBrowserCompat
    private lateinit var mediaId : String
    private lateinit var nowPlayingViewModel : NowPlayingViewModel

    private lateinit var controllerCallbacks : MediaControllerCallbacks
//    private val itemCallback = object : MediaBrowserCompat.ItemCallback() {
//        override fun onItemLoaded(item: MediaBrowserCompat.MediaItem?) {
//            item?.let {
//                nowPlayingViewModel.mediaItem.value = it
//            }
//        }
//
//        override fun onError(itemId: String) {
//            super.onError(itemId)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaId = NowPlayingFragmentArgs.fromBundle(requireArguments()).mediaId
        mediaBrowser = (requireActivity() as MainActivity).getMediaBrowser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        nowPlayingViewModel = ViewModelProvider(this, NowPlayingViewModelFactory(mediaId))
            .get(NowPlayingViewModel::class.java)
        initObservers()

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_now_playing, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = nowPlayingViewModel
        controllerCallbacks = MediaControllerCallbacks(binding)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        MediaControllerCompat.getMediaController(this.requireActivity())
            .registerCallback(controllerCallbacks)
//        mediaBrowser.getItem(mediaId, itemCallback)
    }

    override fun onStop() {
        super.onStop()
        MediaControllerCompat.getMediaController(this.requireActivity())
            ?.unregisterCallback(controllerCallbacks)
    }

    private fun initObservers() {
        nowPlayingViewModel.media.observe(viewLifecycleOwner, {
            updateMediaMetadata(MediaControllerCompat.getMediaController(this@NowPlayingFragment.requireActivity()), it)
        })

        nowPlayingViewModel.playCurrent.observe(viewLifecycleOwner, {
            if (it) {
                handlePlayCurrent(MediaControllerCompat.getMediaController(this@NowPlayingFragment.requireActivity()), nowPlayingViewModel.media.value!!)
                nowPlayingViewModel.reset(ACTION_PLAY_CURRENT)
            }
        })

        nowPlayingViewModel.playPrevious.observe(viewLifecycleOwner, {
            if (it) {
                handlePlayPrevious(MediaControllerCompat.getMediaController(this@NowPlayingFragment.requireActivity()))
                nowPlayingViewModel.reset(ACTION_PLAY_PREVIOUS)
            }
        })

        nowPlayingViewModel.playNext.observe(viewLifecycleOwner, {
            if (it) {
                handlePlayNext(MediaControllerCompat.getMediaController(this@NowPlayingFragment.requireActivity()))
                nowPlayingViewModel.reset(ACTION_PLAY_NEXT)
            }
        })
    }
}