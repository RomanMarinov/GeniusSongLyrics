package com.dev_marinov.geniussonglyrics.domain

data class Song(
    val artistName: String,
    val image: String,
    val title: String,
    val urlPageSong: String,
    val urlPageArtist: Artist
)


