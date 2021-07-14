package com.example.hearmeout.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hearmeout.R
import com.example.hearmeout.data.Song
import com.example.hearmeout.databinding.SongDetailsViewBinding
import com.example.hearmeout.ui.PlaylistFragment

class PlaylistAdapter(val playlistFragment : PlaylistFragment) : RecyclerView.Adapter<PlaylistAdapter.SongDetailsView>() {

    private var songs = listOf<Song>()

    private lateinit var binding : SongDetailsViewBinding

    fun updateList(playlist : List<Song>) {
        songs = playlist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongDetailsView {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.song_details_view, parent, false)
        return SongDetailsView(binding)
    }

    override fun onBindViewHolder(holder: SongDetailsView, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    inner class SongDetailsView(binding : SongDetailsViewBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                playlistFragment.playAudio(songs[bindingAdapterPosition])
            }
        }

        fun bind(song : Song) {
            with (song) {
                Log.i("Aarathi", "title: $title\nimageUrl: $image")
                binding.song = song
                binding.executePendingBindings()
            }
        }
    }
}