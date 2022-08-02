package com.dev_marinov.geniussonglyrics.di

import com.dev_marinov.geniussonglyrics.data.ArtistsSongsRepository
import com.dev_marinov.geniussonglyrics.domain.IArtistSongRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindArtistSongRepository(artistsSongsRepository: ArtistsSongsRepository) : IArtistSongRepository
}

