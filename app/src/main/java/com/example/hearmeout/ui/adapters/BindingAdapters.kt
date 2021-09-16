package com.example.hearmeout.ui.adapters

import android.media.MediaMetadata
import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hearmeout.data.Song
import com.google.android.material.card.MaterialCardView

@BindingAdapter("imageUrl")
fun setImageUrl(imageView : ImageView, url : Uri) {
    /*Glide.with(imageView.context)
        .load(url)
        .into(imageView)*/
}

@BindingAdapter("listData")
fun setListData(recyclerView : RecyclerView, media : List<MediaBrowserCompat.MediaItem>) {
    (recyclerView.adapter as PlaylistAdapter).submitList(media)
}

@BindingAdapter("maxTime")
fun setMaxTime(seekBar : AppCompatSeekBar, maxTime : Long) {
    Log.i("Aarathi","setMaxTime duration: ${maxTime.toInt()}")
    seekBar.min = 0
    seekBar.max = maxTime.toInt()
}
