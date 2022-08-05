package com.dev_marinov.geniussonglyrics.di

import android.util.Log
import com.dev_marinov.geniussonglyrics.data.ArtistsSongsRepository
import com.dev_marinov.geniussonglyrics.data.remote.ArtistsSongsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideArtistsSongsService(retrofit: Retrofit) : ArtistsSongsService{
        Log.e("333","=provideArtistsSongsService=")
        return retrofit.create(ArtistsSongsService::class.java)
    }

    // GET https://genius.p.rapidapi.com/artists/16775/songs?per_page=20

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        Log.e("333","=provideRetrofit=")
        val baseUrl = "https://genius.p.rapidapi.com/artists/16775/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        Log.e("333","=provideHttpClient=")
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(Interceptor { chain ->
                val builder = chain.request().newBuilder()
                builder.header("X-RapidAPI-Host", "genius.p.rapidapi.com")
                builder.header("X-RapidAPI-Key", "c7ab72f3e6msh7e8e62955218901p1a9717jsnc77db06de783")
                return@Interceptor chain.proceed(builder.build())
            })
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
    }
}
