package ru.netology.mediaplayer.api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import ru.netology.mediaplayer.BuildConfig
import ru.netology.mediaplayer.dto.Album

private const val BASE_URL = BuildConfig.BASE_URL

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface MediaApiService {
    @GET("album.json")
    suspend fun loadAlbum(): Response<Album>
}

object Api {
    val service: MediaApiService by lazy {
        retrofit.create(MediaApiService::class.java)
    }
}