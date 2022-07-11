package ru.netology.mediaplayer.dto

data class Track(
    val id: Int,
    val file: String,
    val titleAlbum: String?,
    val selected: Boolean,
    val isPlaying: Boolean
)