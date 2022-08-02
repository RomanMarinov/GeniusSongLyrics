package com.dev_marinov.geniussonglyrics.domain

interface IArtistSongRepository {
    suspend fun getArtistsSongs(num: Int): List<Song>
}