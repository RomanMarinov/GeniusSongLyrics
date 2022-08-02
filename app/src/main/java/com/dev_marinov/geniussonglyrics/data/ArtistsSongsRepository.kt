package com.dev_marinov.geniussonglyrics.data

import android.util.Log
import com.dev_marinov.geniussonglyrics.data.remote.ArtistsSongsService
import com.dev_marinov.geniussonglyrics.data.remote.dto.GetSongsResponseDTO
import com.dev_marinov.geniussonglyrics.domain.IArtistSongRepository
import com.dev_marinov.geniussonglyrics.domain.Song
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistsSongsRepository @Inject constructor(private val remoteDataSource: ArtistsSongsService) : IArtistSongRepository{

    override suspend fun getArtistsSongs(num: Int): List<Song> {
        val response = remoteDataSource.getArtistsSongs(num = num)
        val artistsSongs = response.body()?.let {
            it.response.songs.map { song ->
                song.mapToDomain()
            }
        } ?: listOf()

        return artistsSongs
    }
}