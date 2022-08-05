package com.dev_marinov.geniussonglyrics.data.remote.dto

import com.dev_marinov.geniussonglyrics.domain.Song
import com.google.gson.annotations.SerializedName

data class SongDTO(
    @SerializedName("artist_names")
    val artistName: String,
    @SerializedName("header_image_thumbnail_url")
    val image: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val urlPageSong: String,
    @SerializedName("primary_artist")
    val urlPageArtist: ArtistDTO
) {

    fun mapToDomain() : Song {
        return Song(
            artistName = artistName,
            image = image,
            title = title,
            urlPageSong = urlPageSong,
            urlPageArtist = urlPageArtist.mapToDomain()
        )
    }
}



