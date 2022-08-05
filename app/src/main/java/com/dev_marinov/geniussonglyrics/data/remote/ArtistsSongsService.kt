package com.dev_marinov.geniussonglyrics.data.remote

import com.dev_marinov.geniussonglyrics.data.remote.dto.GetSongsResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ArtistsSongsService {

    // GET https://genius.p.rapidapi.com/artists/16775/songs?per_page=20

    @GET("songs")
    suspend fun getArtistsSongs(
        @Query("per_page") num: Int
    ): Response<GetSongsResponseDTO>
}