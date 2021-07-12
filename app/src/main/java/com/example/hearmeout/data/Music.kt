package com.example.hearmeout.data

import com.squareup.moshi.Json

data class Music (
    @Json(name = "music")
    val songs : List<Song>)