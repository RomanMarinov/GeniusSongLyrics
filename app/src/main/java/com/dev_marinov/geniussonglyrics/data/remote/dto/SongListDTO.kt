package com.dev_marinov.geniussonglyrics.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SongListDTO(
    @SerializedName("songs")
    val songs: List<SongDTO>
)
