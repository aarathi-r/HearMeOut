package com.example.hearmeout.viewmodel

import android.media.MediaMetadata
import android.support.v4.media.MediaDescriptionCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hearmeout.util.ACTION_PLAY_CURRENT
import com.example.hearmeout.util.ACTION_PLAY_NEXT
import com.example.hearmeout.util.ACTION_PLAY_PREVIOUS

class NowPlayingViewModel(mediaDescription : MediaDescriptionCompat) : ViewModel() {

    var media = MutableLiveData(mediaDescription)
    val duration = MutableLiveData(mediaDescription.extras?.get(MediaMetadata.METADATA_KEY_DURATION) as Long)

    private var _playCurrent = MutableLiveData(true)
    val playCurrent : LiveData<Boolean>
        get() = _playCurrent

    private var _playPrevious = MutableLiveData(false)
    val playPrevious : LiveData<Boolean>
        get() = _playPrevious

    private var _playNext = MutableLiveData(false)
    val playNext : LiveData<Boolean>
        get() = _playNext

    fun playPrevious() {
        _playPrevious.value = true
    }

    fun playNext() {
        _playNext.value = true
    }

    fun playCurrent() {
        _playCurrent.value = true
    }

    fun reset(action : Int) {
        when(action) {
            ACTION_PLAY_CURRENT -> _playCurrent.value = false
            ACTION_PLAY_PREVIOUS -> _playPrevious.value = false
            ACTION_PLAY_NEXT -> _playNext.value = false
        }
    }
}