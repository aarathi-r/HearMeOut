package com.example.hearmeout.playback

import android.content.Context
import android.content.Intent
import android.media.*
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log

class MediaSessionCallbacks(private val service : MediaPlaybackService, private val mediaSession : MediaSessionCompat) : MediaSessionCompat.Callback() {

    private var mediaPlayer : MediaPlayer = MediaPlayer()
    private val playbackActions = PlaybackActions(mediaPlayer)

    private var audioManager : AudioManager =
        service.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private lateinit var audioFocusRequest: AudioFocusRequest
//    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focus ->
//        when(focus) {
//            AudioManager.AUDIOFOCUS_GAIN -> Log.i("Aarathi","Audio in focus currently")
//            AudioManager.AUDIOFOCUS_LOSS -> Log.i("Aarathi","Audio not in focus anymore")
//            else -> Log.i("Aarathi","The audio focus state is : $focus" )
//        }
//    }

    private var curPos : Int = 0

    private fun startPlayer() {
        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
            //setOnAudioFocusChangeListener(audioFocusChangeListener)
            setAudioAttributes(AudioAttributes.Builder().run {
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                build() })
            build()
        }

        val audioFocus = audioManager.requestAudioFocus(audioFocusRequest)
        if (audioFocus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            service.startService(Intent(service.applicationContext, MediaPlaybackService::class.java))
            mediaSession.apply {
                isActive = true
                setPlaybackState(playbackActions.getPlaybackState(PlaybackStateCompat.STATE_PLAYING))
                setMetadata(playbackActions.getMetadata())
            }
            mediaPlayer.start()
        }
    }

    override fun onPlay() {
        Log.i("Aarathi","Play button is pressed")
        startPlayer()
    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        Log.i("Aarathi","PlayFromMediaId button is pressed\nmediaId = $mediaId")
        mediaId?.let { playbackActions.play(it, curPos) }
        startPlayer()
    }

    override fun onPause() {
        Log.i("Aarathi","Pause button is pressed")
        mediaSession.setPlaybackState(playbackActions.getPlaybackState(PlaybackStateCompat.STATE_PAUSED))
        curPos = mediaPlayer.currentPosition
        mediaPlayer.pause()
    }

    override fun onStop() {
        audioManager.abandonAudioFocusRequest(audioFocusRequest)
        mediaPlayer.apply {
            stop()
            release()
        }
        mediaSession.apply {
            setPlaybackState(playbackActions.getPlaybackState(PlaybackStateCompat.STATE_STOPPED))
            isActive = false
        }
        service.stopSelf()
    }

    override fun onSkipToPrevious() {
        playbackActions.skipToPrevious()
        startPlayer()
    }

    override fun onSkipToNext() {
        playbackActions.skipToNext()
        startPlayer()
    }
}