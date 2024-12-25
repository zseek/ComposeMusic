package com.github.composemusic.route.nav.recommend.playlist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.composemusic.APP
import com.github.composemusic.network.MusicApiService
import com.github.composemusic.network.RemoteResult
import com.github.composemusic.network.baseApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RePlaylistViewModel @Inject constructor(private val service: MusicApiService) : ViewModel() {
    private val _uiStatus = mutableStateOf(RePlaylistUIStatus())
    val uiStatus: State<RePlaylistUIStatus> = _uiStatus

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            getRecommendPlaylist()
            getRecommendEveryDayPlaylist()
        }
    }

    /**网易云推荐歌单*/
    private suspend fun getRecommendPlaylist() {
        val response = baseApiCall { service.getRecommendPlaylist() }
        when (response) {
            is RemoteResult.Success -> {
                if (response.data.code == 200) {
                    _uiStatus.value = uiStatus.value.copy(
                        newPlaylist = response.data.result
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

    /**获取个人每日推荐的歌单*/
    private suspend fun getRecommendEveryDayPlaylist() {
        val response = baseApiCall { service.getRecommendEveryDayPlaylist(APP.cookie) }
        when (response) {
            is RemoteResult.Success -> {
                if (response.data.code == 200) {
                    _uiStatus.value = uiStatus.value.copy(
                        dayPlaylist = response.data.recommend
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

