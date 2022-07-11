package ru.netology.mediaplayer.repository

import ru.netology.mediaplayer.dto.Album

interface MediaRepository {
    suspend fun loadAlbum(): Album
}