package com.dev_marinov.geniussonglyrics.data.remote.dto

import com.dev_marinov.geniussonglyrics.domain.Artist
import com.dev_marinov.geniussonglyrics.domain.Song

data class ArtistDTO(
    val url: String
) {
    fun mapToDomain() : Artist {
        return Artist(url = url)
    }
}
