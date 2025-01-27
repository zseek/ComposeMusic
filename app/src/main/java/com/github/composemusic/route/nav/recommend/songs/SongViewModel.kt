package com.github.composemusic.route.nav.recommend.songs

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.composemusic.APP
import com.github.composemusic.bean.song.SongMediaBean
import com.github.composemusic.network.MusicApiService
import com.github.composemusic.network.RemoteResult
import com.github.composemusic.network.baseApiCall
import com.github.composemusic.route.musicplayer.service.MusicServiceHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val service: MusicApiService,
    private val musicServiceHandler: MusicServiceHandler //

) : ViewModel() {
    private val _uiStatus = mutableStateOf(SongUIStatus())
    val uiStatus: State<SongUIStatus> = _uiStatus

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            getRecommendSongs()
            getRecommendEveryDaySongs()
        }
    }

    fun onEvent(event: SongEvent) {
        viewModelScope.launch {
            when (event) {
                is SongEvent.InsertDaySong -> {
                    musicServiceHandler.isExistPlaylist(
                        SongMediaBean(
                            createTime = System.currentTimeMillis(),
                            songID = event.bean.id,
                            songName = event.bean.name,
                            cover = event.bean.al.picUrl,
                            artist = event.bean.ar[0].name,
                            url = "",
                            isLoading = false,
                            duration = 0L,
                            size = ""
                        )
                    )
                }

                is SongEvent.InsertNewSong -> {
                    musicServiceHandler.isExistPlaylist(
                        SongMediaBean(
                            createTime = System.currentTimeMillis(),
                            songID = event.bean.id,
                            songName = event.bean.name,
                            cover = event.bean.picUrl,
                            artist = event.bean.song.artists[0].name,
                            url = "",
                            isLoading = false,
                            duration = 0L,
                            size = ""
                        )
                    )
                }
            }
        }
    }

    /**获取推荐新音乐*/
    private suspend fun getRecommendSongs() {
        val response = baseApiCall { service.getRecommendSongs(APP.cookie) }
        when (response) {
            is RemoteResult.Success -> {
                if (response.data.code == 200) {
                    _uiStatus.value = uiStatus.value.copy(
                        newSongs = response.data.result
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

    /**获取每日推荐的歌曲*/
    private suspend fun getRecommendEveryDaySongs() {
        val response = baseApiCall { service.getRecommendEveryDaySongs(APP.cookie) }
        when (response) {
            is RemoteResult.Success -> {
                if (response.data.code == 200) {
                    _uiStatus.value = uiStatus.value.copy(
                        daySongs = response.data.data.dailySongs
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