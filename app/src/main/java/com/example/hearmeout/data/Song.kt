package com.example.hearmeout.data

data class Song(
    val title: String,
    val album: String,
    val artist: String,
    val genre: String,
    var source: String,
    var image: String,
    val duration: Int,
    val site: String)