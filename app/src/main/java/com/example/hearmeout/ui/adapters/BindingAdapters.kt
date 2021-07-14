package com.example.hearmeout.ui.adapters

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hearmeout.data.Song

@BindingAdapter("imageUrl")
fun setImageUrl(imageView : ImageView, url : String) {
    Glide.with(imageView.context)
        .load(url.toUri())
        .into(imageView)
}

@BindingAdapter("listData")
fun setListData(recyclerView : RecyclerView, songs : List<Song>) {
    (recyclerView.adapter as PlaylistAdapter).updateList(songs)
}
