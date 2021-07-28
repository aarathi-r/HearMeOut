package com.example.hearmeout.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hearmeout.viewmodel.NowPlayingViewModel

@Suppress("UNCHECKED_CAST")
class NowPlayingViewModelFactory(private val mediaId : String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NowPlayingViewModel::class.java)) {
            return NowPlayingViewModel(mediaId) as T
        }
        throw IllegalArgumentException("Not a valid ViewModel class !")
    }
}