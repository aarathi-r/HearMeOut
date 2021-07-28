package com.example.hearmeout.ui

import android.content.ComponentName
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import androidx.navigation.findNavController
import com.example.hearmeout.R
import com.example.hearmeout.playback.MediaPlaybackService

class MainActivity : AppCompatActivity() {

    private lateinit var mediaBrowser: MediaBrowserCompat

    private val connCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            Log.i("Aarathi", "MainActivity - MediaBrowser connected to the service")
            val mediaController = MediaControllerCompat(
                this@MainActivity, mediaBrowser.sessionToken)

            MediaControllerCompat.setMediaController(
                this@MainActivity, mediaController)

            Log.i("Aarathi", "onConnected (connCallback) - mediaRoot: ${mediaBrowser.root}")
            val startArgs = Bundle().apply { putString("mediaRoot", mediaBrowser.root) }
            findNavController(R.id.nav_host_fragment).setGraph(R.navigation.players_navigation_graph, startArgs)
        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i("Aarathi", "MainActivity - onCreate")
        mediaBrowser = MediaBrowserCompat(
            this,
            ComponentName(this, MediaPlaybackService::class.java),
            connCallback,
            null
        )
    }

    override fun onStart() {
        super.onStart()
        Log.i("Aarathi", "MainActivity - onStart")
        mediaBrowser.connect()
    }

    override fun onStop() {
        super.onStop()
        mediaBrowser.disconnect()
    }

    fun getMediaBrowser() : MediaBrowserCompat {
        return mediaBrowser
    }
}