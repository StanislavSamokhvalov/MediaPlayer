package ru.netology.mediaplayer.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.mediaplayer.dto.Album
import ru.netology.mediaplayer.repository.MediaRepository
import ru.netology.mediaplayer.repository.MediaRepositoryImpl

class MediaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MediaRepository = MediaRepositoryImpl()

    private val _data = MutableLiveData<Album>()
    val data: LiveData<Album>
        get() = _data

    val playId = MutableLiveData<Int>()

    init {
        loadAlbum()
    }

    private fun loadAlbum() = viewModelScope.launch {
        val album = repository.loadAlbum()
        _data.value = album
    }

    fun play(id: Int) {
        playId.value = id
        _data.value = _data.value?.let { album ->
            album.copy(tracks = album.tracks.map { track ->
                if (id == track.id) {
                    track.copy(isPlaying = !track.isPlaying, selected = true)
                } else {
                    track.copy(isPlaying = false, selected = false)
                }
            })
        }
    }

    fun isPlaying(): Boolean {
        return data.value?.tracks?.filter { track ->
            track.isPlaying
        }.isNullOrEmpty()
    }
}