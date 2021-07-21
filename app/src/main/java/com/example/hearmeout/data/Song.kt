package com.example.hearmeout.data

import com.squareup.moshi.Json

data class Song(
    val title: String,
    val album: String,
    val artist: String,
    val genre: String,
    var source: String,
    var image: String,
    val trackNumber : Long,
    @Json(name = "totalTrackCount")
    val trackCount : Long,
    val duration: Long)