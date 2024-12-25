package com.github.composemusic.route.nav.recommend.albums

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.composemusic.network.MusicApiService
import com.github.composemusic.network.RemoteResult
import com.github.composemusic.network.baseApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(private val service: MusicApiService) : ViewModel() {
    private val _uiStatus = mutableStateOf(AlbumUIStatus())
    val uiStatus: State<AlbumUIStatus> = _uiStatus

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getRecommendAlbums()
            getRecommendDigitAlbums()
            getRecommendAlbumsRank()
        }
    }

    /**获取最新专辑*/
    private suspend fun getRecommendAlbums() {
        val response = baseApiCall { service.getNewestAlbums() }
        when (response) {
            is RemoteResult.Success -> {
                if (response.data.code == 200) {
                    _uiStatus.value = uiStatus.value.copy(
                        albums = response.data.albums
                    )
                } else {
                    _eventFlow.emit("The error code is ${response.data.code}")
                }
            }

            is RemoteResult.Error -> {
                _eventFlow.emit(response.exception.message.toString())
            }
        }
    }

    /**获取数字专辑*/
    private suspend fun getRecommendDigitAlbums() {
        val response = baseApiCall { service.getDigitNewestAlbums() }
        when (response) {
            is RemoteResult.Success -> {
                if (response.data.code == 200) {
                    _uiStatus.value = uiStatus.value.copy(
                        digitAlbums = response.data.products
                    )
                } else {
                    _eventFlow.emit("The error code is ${response.data.code}")
                }
            }

            is RemoteResult.Error -> {
                _eventFlow.emit(response.exception.message.toString())
            }
        }
    }

    /**数字专辑榜单*/
    private suspend fun getRecommendAlbumsRank() {
        val response = baseApiCall { service.getDigitAlbumsRank() }
        when (response) {
            is RemoteResult.Success -> {
                if (response.data.code == 200) {
                    _uiStatus.value = uiStatus.value.copy(
                        albumsRank = response.data.products
                    )
                } else {
                    _eventFlow.emit("The error code is ${response.data.code}")
                }
            }

            is RemoteResult.Error -> {
                _eventFlow.emit(response.exception.message.toString())
            }
        }
    }
}