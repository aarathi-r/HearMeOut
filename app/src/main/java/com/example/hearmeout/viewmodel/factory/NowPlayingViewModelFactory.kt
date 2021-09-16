package com.example.hearmeout.viewmodel.factory

import android.support.v4.media.MediaDescriptionCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hearmeout.viewmodel.NowPlayingViewModel

@Suppress("UNCHECKED_CAST")
class NowPlayingViewModelFactory(private val media : MediaDescriptionCompat) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NowPlayingViewModel::class.java)) {
            return NowPlayingViewModel(media) as T
        }
        throw IllegalArgumentException("Not a valid ViewModel class !")
    }
}