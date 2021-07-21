package com.example.hearmeout.ui.adapters

import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hearmeout.R
import com.example.hearmeout.databinding.MediaDetailsViewBinding

class PlaylistAdapter(private val clickListener: MediaClickListener)
    : ListAdapter<MediaBrowserCompat.MediaItem, PlaylistAdapter.MediaDetailsView>(PlaylistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaDetailsView {
        return MediaDetailsView.create(parent)
    }

    override fun onBindViewHolder(holder: MediaDetailsView, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class MediaDetailsView(private val binding: MediaDetailsViewBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup) : MediaDetailsView {
                val viewBinding = DataBindingUtil.inflate<MediaDetailsViewBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.media_details_view, parent, false)
                return MediaDetailsView(viewBinding)
            }
        }

        fun bind(media : MediaBrowserCompat.MediaItem, clickHandler : MediaClickListener) {
            Log.i("Aarathi", "title: ${media.description.title}\nimageUrl: ${media.description.iconUri}")
            with (binding) {
                mediaItem = media
                clickListener = clickHandler
                executePendingBindings()
            }
        }
    }
}

class PlaylistDiffCallback : DiffUtil.ItemCallback<MediaBrowserCompat.MediaItem>() {
    override fun areItemsTheSame(oldItem: MediaBrowserCompat.MediaItem, newItem: MediaBrowserCompat.MediaItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: MediaBrowserCompat.MediaItem, newItem: MediaBrowserCompat.MediaItem): Boolean {
        return oldItem.equals(newItem)
    }
}

class MediaClickListener(val clickListener: (media: MediaBrowserCompat.MediaItem) -> Unit){
    fun onClick(media : MediaBrowserCompat.MediaItem) = clickListener(media)
}