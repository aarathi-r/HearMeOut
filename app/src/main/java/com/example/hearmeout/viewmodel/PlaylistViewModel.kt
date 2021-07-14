package com.example.hearmeout.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hearmeout.data.Song
import com.example.hearmeout.data.SongProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistViewModel : ViewModel() {

    private val songProvider = SongProvider()
    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(coroutineJob + Dispatchers.Main)
    private var _songs = MutableLiveData(listOf<Song>())
    val songs: LiveData<List<Song>>
        get() = _songs

    init {
        fetchSongs()
    }

    private fun fetchSongs() {
        Log.i("Aarathi", "Fetch the Songs")
        coroutineScope.launch {
            try {
                _songs.value = songProvider.fetchSongs()
            } catch (t : Throwable) {

            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob.cancel()
    }
}