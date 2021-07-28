package com.example.hearmeout.viewmodel

import android.support.v4.media.MediaDescriptionCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.hearmeout.data.SongProvider
import com.example.hearmeout.util.ACTION_PLAY_CURRENT
import com.example.hearmeout.util.ACTION_PLAY_NEXT
import com.example.hearmeout.util.ACTION_PLAY_PREVIOUS

class NowPlayingViewModel(id : String) : ViewModel() {

    var mediaId = MutableLiveData(id)

    val media : LiveData<MediaDescriptionCompat> = Transformations.map(mediaId) { id ->
        SongProvider.getMetadataForMediaId(id)?.description
    }

    private var _playCurrent = MutableLiveData(false)
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