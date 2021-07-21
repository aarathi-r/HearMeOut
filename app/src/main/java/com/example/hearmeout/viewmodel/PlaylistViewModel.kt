package com.example.hearmeout.viewmodel

import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hearmeout.data.Song

class PlaylistViewModel : ViewModel() {

    private var _songs = MutableLiveData(listOf<Song>())
    val songs: LiveData<List<Song>>
        get() = _songs

    fun refreshSongs(media : List<Song>) {
        Log.i("Aarathi", "refreshSongs - ${media.size}")
        _songs.value = media
    }

    private var _mediaItems = MutableLiveData(listOf<MediaBrowserCompat.MediaItem>())
    val mediaItems : LiveData<List<MediaBrowserCompat.MediaItem>>
        get() = _mediaItems

    fun refreshMediaItems(media : List<MediaBrowserCompat.MediaItem>) {
        _mediaItems.value = media
    }
}