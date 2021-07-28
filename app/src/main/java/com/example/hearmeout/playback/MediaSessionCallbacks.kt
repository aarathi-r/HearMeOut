package com.example.hearmeout.playback

import android.content.Context
import android.content.Intent
import android.media.*
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.example.hearmeout.R

class MediaSessionCallbacks(private val service : MediaPlaybackService) : MediaSessionCompat.Callback() {

    private var mediaPlayer : MediaPlayer = initPlayer()
    private val playbackActions = PlaybackActions(mediaPlayer)

    private var audioManager : AudioManager =
        service.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private lateinit var audioFocusRequest: AudioFocusRequest

    private var mediaList = listOf<MediaBrowserCompat.MediaItem>()

    private var curPos : Int = 0

    //TODO - update the logic
    fun initMediaList(media : List<MediaBrowserCompat.MediaItem>) {
        mediaList = media
    }

    private fun initPlayer() : MediaPlayer {
        return MediaPlayer()
    }

    private fun startPlayer() {
        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
            setOnAudioFocusChangeListener { focus ->
                when(focus) {
                    AudioManager.AUDIOFOCUS_GAIN -> Log.i("Aarathi","Audio in focus currently")
                    AudioManager.AUDIOFOCUS_LOSS -> Log.i("Aarathi","Audio not in focus anymore")
                    else -> Log.i("Aarathi","The audio focus state is : $focus" )
                }
            }
            setAudioAttributes(AudioAttributes.Builder().run {
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                build() })
            build()
        }

        val audioFocus = audioManager.requestAudioFocus(audioFocusRequest)
        if (audioFocus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            service.startService(Intent(service.applicationContext, MediaPlaybackService::class.java))
            service.getMediaSession()?.apply {
                isActive = true
                setPlaybackState(getPlaybackState(PlaybackStateCompat.STATE_PLAYING))
                //setMetadata(getMetadata())
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
        service.getMediaSession()?.setPlaybackState(getPlaybackState(PlaybackStateCompat.STATE_PAUSED))
        curPos = mediaPlayer.currentPosition
        mediaPlayer.pause()
    }

    override fun onStop() {
        audioManager.abandonAudioFocusRequest(audioFocusRequest)
        mediaPlayer.apply {
            stop()
            release()
        }
        service.getMediaSession()?.apply {
            setPlaybackState(getPlaybackState(PlaybackStateCompat.STATE_STOPPED))
            isActive = false
        }
        service.stopSelf()
    }

    private fun getPlaybackState(state : Int) : PlaybackStateCompat {
        return PlaybackStateCompat.Builder().run {
            setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1.0F)
            build()
        }
    }

    private fun getMetadata() : MediaMetadataCompat {
        var title : String?
        MediaMetadataRetriever().apply {
            val afd = service.resources.openRawResourceFd(R.raw.rozana)
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            title = extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        }

        return MediaMetadataCompat.Builder().run {
            putString(MediaMetadata.METADATA_KEY_TITLE, title ?: "Title unknown")
            build()
        }
    }

    override fun onSkipToPrevious() {

    }

    override fun onSkipToNext() {

    }
}