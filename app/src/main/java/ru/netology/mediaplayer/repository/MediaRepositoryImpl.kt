package ru.netology.mediaplayer.repository

import ru.netology.mediaplayer.api.Api
import ru.netology.mediaplayer.dto.Album
import ru.netology.mediaplayer.error.ApiError
import ru.netology.mediaplayer.error.NetworkError
import ru.netology.mediaplayer.error.UnknownError
import java.io.IOException

class MediaRepositoryImpl: MediaRepository {
    override suspend fun loadAlbum(): Album {
        try {
            val response = Api.service.loadAlbum()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw Exception()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}