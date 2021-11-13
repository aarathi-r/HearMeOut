package com.example.hearmeout.playback

import android.media.MediaMetadata
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.appcompat.widget.AppCompatSeekBar
import com.example.hearmeout.R
import com.example.hearmeout.databinding.FragmentNowPlayingBinding
import com.example.hearmeout.viewmodel.NowPlayingViewModel
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

private const val PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 500L

class MediaControllerCallbacks(
    private val binding : FragmentNowPlayingBinding,
    private val viewModel : NowPlayingViewModel,
    private val mediaController : MediaControllerCompat) : MediaControllerCompat.Callback() {
    private val executor = Executors.newSingleThreadScheduledExecutor()
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
        initSeekBar(binding.songProgress)
    }

    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        Log.i("Aarathi","Metadata is changed")
        viewModel.media.value = metadata?.description
        viewModel.duration.value = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION)
    }

    override fun onAudioInfoChanged(info: MediaControllerCompat.PlaybackInfo?) {
    }

    override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
        super.onQueueChanged(queue)
    }

    private fun initSeekBar(seekBar : AppCompatSeekBar) {
        val playbackState = mediaController.playbackState
        var position = playbackState.position
        Log.i("Aarathi","state ${playbackState.state}")
        val runnable = Runnable {
            val timeDelta = SystemClock.elapsedRealtime() -
                    playbackState.lastPositionUpdateTime
            position += (timeDelta * playbackState.playbackSpeed).toLong()
            position /= 1000
            Log.i("Aarathi","position $position")
            seekBar.progress = position.toInt()
        }
        if (playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
            executor?.scheduleAtFixedRate(
                runnable,
                0,
                PLAYBACK_POSITION_REFRESH_INTERVAL_MS,
                TimeUnit.MILLISECONDS)
        } else {
            executor?.shutdownNow()
        }
    }
}