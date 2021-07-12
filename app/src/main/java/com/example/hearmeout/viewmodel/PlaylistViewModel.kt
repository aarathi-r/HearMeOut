package com.example.hearmeout.viewmodel

import androidx.lifecycle.ViewModel
import com.example.hearmeout.data.SongProvider

class PlaylistViewModel : ViewModel() {

    private val songProvider = SongProvider()

    fun fetchSongs() {
        songProvider.fetchSongsFromNetwork()
    }

}