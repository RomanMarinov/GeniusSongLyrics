package com.dev_marinov.geniussonglyrics.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GetSongsResponseDTO(
    @SerializedName("response")
    val response: SongListDTO
)