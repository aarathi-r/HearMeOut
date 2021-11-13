package com.example.hearmeout.ui

import android.os.Bundle
import android.os.SystemClock
import androidx.media2.session.MediaController
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatSeekBar
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
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class NowPlayingFragment : Fragment() {

    private lateinit var binding : FragmentNowPlayingBinding

    private lateinit var mediaBrowser : MediaBrowserCompat
    private lateinit var media : MediaDescriptionCompat
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
        media = NowPlayingFragmentArgs.fromBundle(requireArguments()).mediaDescription
        mediaBrowser = (requireActivity() as MainActivity).getMediaBrowser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        nowPlayingViewModel = ViewModelProvider(this, NowPlayingViewModelFactory(media))
            .get(NowPlayingViewModel::class.java)
        initObservers()

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_now_playing, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = nowPlayingViewModel
        controllerCallbacks = MediaControllerCallbacks(binding, nowPlayingViewModel,
            MediaControllerCompat.getMediaController(this.requireActivity()))
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
//        nowPlayingViewModel.media.observe(viewLifecycleOwner, {
//            updateMediaMetadata(this@NowPlayingFragment.requireActivity(), it.mediaId!!)
//        })

        nowPlayingViewModel.playCurrent.observe(viewLifecycleOwner, {
            if (it) {
                handlePlayCurrent(this@NowPlayingFragment.requireActivity(), nowPlayingViewModel.media.value?.mediaId!!)
                nowPlayingViewModel.reset(ACTION_PLAY_CURRENT)
            }
        })

        nowPlayingViewModel.playPrevious.observe(viewLifecycleOwner, {
            if (it) {
                handlePlayPrevious(this@NowPlayingFragment.requireActivity())
                nowPlayingViewModel.reset(ACTION_PLAY_PREVIOUS)
            }
        })

        nowPlayingViewModel.playNext.observe(viewLifecycleOwner, {
            if (it) {
                handlePlayNext(this@NowPlayingFragment.requireActivity())
                nowPlayingViewModel.reset(ACTION_PLAY_NEXT)
            }
        })
    }
}