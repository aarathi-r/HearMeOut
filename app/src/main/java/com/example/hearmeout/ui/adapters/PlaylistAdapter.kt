package com.example.hearmeout.ui.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hearmeout.R
import com.example.hearmeout.data.Song
import com.example.hearmeout.ui.PlaylistFragment

class PlaylistAdapter(val playlistFragment : PlaylistFragment) : RecyclerView.Adapter<PlaylistAdapter.SongDetailsView>() {

    private var songs = listOf<Song>()

    fun updateList(playlist : List<Song>) {
        songs = playlist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongDetailsView {
        //val view = View.inflate(context, R.layout.song_details_view, parent, false)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_details_view, parent, false)
        return SongDetailsView(view)
    }

    override fun onBindViewHolder(holder: SongDetailsView, position: Int) {
        with (songs[position]) {
            Log.i("Aarathi", "title: $title\nimageUrl: $image")
            holder.title.text = title
            holder.art.setImageURI(Uri.parse(image))
        }
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    inner class SongDetailsView(view : View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val art: ImageView = view.findViewById(R.id.art)
        init {
            view.setOnClickListener {
                playlistFragment.playAudio(songs[bindingAdapterPosition])
            }
        }
    }
}