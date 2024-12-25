package com.github.composemusic.route.drawer.recent

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.composemusic.APP
import com.github.composemusic.bean.mv.MvBean
import com.github.composemusic.bean.recent.RecentMlogBean
import com.github.composemusic.bean.recent.RecentVideoBean
import com.github.composemusic.bean.song.SongMediaBean
import com.github.composemusic.network.MusicApiService
import com.github.composemusic.network.RemoteResult
import com.github.composemusic.network.baseApiCall
import com.github.composemusic.route.musicplayer.service.MusicServiceHandler
import com.github.composemusic.route.playlist.NetworkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentPlayViewModel @Inject constructor(
    private val service: MusicApiService,
    private val musicServiceHandler: MusicServiceHandler
) : ViewModel() {
    private val _uiState = mutableStateOf(RecentPlayUIState())
    val uiState: State<RecentPlayUIState> = _uiState

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getRecentSongs()
            getRecentPlaylists()
            getRecentAlbums()
            getRecentVideos()
            getRecentDjs()
        }
    }

    fun onEvent(event: RecentPlayEvent) {
        viewModelScope.launch(Dispatchers.Main) {
            when (event) {
                is RecentPlayEvent.PlaySong -> {
                    musicServiceHandler.isExistPlaylist(
                        SongMediaBean(
                            createTime = System.currentTimeMillis(),
                            songID = _uiState.value.songs[event.index].data.id,
                            songName = _uiState.value.songs[event.index].data.name,
                            cover = _uiState.value.songs[event.index].data.al.picUrl,
                            artist = _uiState.value.songs[event.index].data.ar[0].name,
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

    /**获取最近播放的歌曲*/
    private suspend fun getRecentSongs() {
        val response = baseApiCall { service.getRecentSongs(APP.cookie) }
        when (response) {
            is RemoteResult.Success -> {
                if (response.data.code == 200) {
                    _uiState.value = uiState.value.copy(
                        songs = response.data.data.list,
                        songState = NetworkStatus.Successful
                    )
                } else {
                    _uiState.value = uiState.value.copy(
                        songState = NetworkStatus.Failed("The error code is ${response.data.code}")
                    )
                    _eventFlow.emit("The error code is ${response.data.code}")
                }
            }
            is RemoteResult.Error -> {
                _uiState.value = uiState.value.copy(
                    songState = NetworkStatus.Failed(response.exception.message.toString())
                )
                _eventFlow.emit(response.exception.message.toString())
            }
        }
    }

    /**获取最近播放的歌单*/
    private suspend fun getRecentPlaylists() {
        val response = baseApiCall { service.getRecentPlaylists(APP.cookie) }
        when (response) {
            is RemoteResult.Success -> {
                if (response.data.code == 200) {
                    _uiState.value = uiState.value.copy(
                        playlists = response.data.data.list,
                        playlistState = NetworkStatus.Successful
                    )
                } else {
                    _uiState.value = uiState.value.copy(
                        playlistState = NetworkStatus.Failed("The error code is ${response.data.code}")
                    )
                    _eventFlow.emit("The error code is ${response.data.code}")
                }
            }

            is RemoteResult.Error -> {
                _uiState.value = uiState.value.copy(
                    playlistState = NetworkStatus.Failed(response.exception.message.toString())
                )
                _eventFlow.emit(response.exception.message.toString())
            }
        }
    }

    /**获取最近播放的专辑*/
    private suspend fun getRecentAlbums() {
        val response = baseApiCall { service.getRecentAlbums(APP.cookie) }
        when (response) {
            is RemoteResult.Success -> {
                if (response.data.code == 200) {
                    _uiState.value = uiState.value.copy(
                        albums = response.data.data.list,
                        albumState = NetworkStatus.Successful
                    )
                } else {
                    _uiState.value = uiState.value.copy(
                        albumState = NetworkStatus.Failed("The error code is ${response.data.code}")
                    )
                    _eventFlow.emit("The error code is ${response.data.code}")
                }
            }

            is RemoteResult.Error -> {
                _uiState.value = uiState.value.copy(
                    albumState = NetworkStatus.Failed(response.exception.message.toString())
                )
                _eventFlow.emit(response.exception.message.toString())
            }
        }
    }

/*
    private suspend fun getRecentVideos() {
        val response = baseApiCall { service.getRecentVideos(APP.cookie) }
        when (response) {
            is RemoteResult.Success -> {
                if (response.data.code == 200) {
                    response.data.data.list.map {
                        if (it.resourceType == "MV") { // MV类型
                            val json = Gson().toJson(it.data)
                            val data = GsonFormat.fromJson(json.toString(), MvBean::class.java)
                            RecentVideoBean(
                                id = data.id,
                                idStr = "",
                                name = data.name,
                                artist = data.artists[0].name,
                                cover = data.coverUrl,
                                duration = data.duration,
                                tag = "MV"
                            )
                        }
                        else { // MLOG类型
                            val json = Gson().toJson(it.data)
                            val data = GsonFormat.fromJson(json.toString(), RecentMlogBean::class.java)
                            RecentVideoBean(
                                id = 0L,
                                idStr = data.id,
                                name = data.title,
                                artist = data.creator.nickname,
                                cover = data.coverUrl,
                                duration = data.duration,
                                tag = "MLOG"
                            )
                        }
                    }.also {
                        _uiState.value = uiState.value.copy(
                            videos = it,
                            videoState = NetworkStatus.Successful
                        )
                    }
                } else {
                    _uiState.value = uiState.value.copy(
                        videoState = NetworkStatus.Failed("The error code is ${response.data.code}")
                    )
                    _eventFlow.emit("The error code is ${response.data.code}")
                }
            }
            is RemoteResult.Error -> {
                _uiState.value = uiState.value.copy(
                    videoState = NetworkStatus.Failed(response.exception.message.toString())
                )
                _eventFlow.emit(response.exception.message.toString())
            }
        }
    }

 */

    /**获取最近播放的视频, 此处返回的视频分为MV和MLOG两种, 所返回的JSON字段也存在差异*/
    private suspend fun getRecentVideos() {
        val response = baseApiCall { service.getRecentVideos(APP.cookie) }
        when (response) {
            is RemoteResult.Success -> {
                if (response.data.code == 200) {
                    val videoList = response.data.data.list.mapNotNull {
                        when (it.resourceType) {
                            // 将it.data转为MvBean类型, 转换失败则返回null, 转换成功则执行let中的内容返回RecentVideoBean类型到videoList
                            "MV" -> (it.data as? MvBean)?.let { data ->
                                RecentVideoBean(
                                    id = data.id,
                                    idStr = "",
                                    name = data.name,
                                    artist = data.artists[0].name,
                                    cover = data.coverUrl,
                                    duration = data.duration,
                                    tag = "MV"
                                )
                            }
                            // 同理, 同上
                            "MLOG" -> (it.data as? RecentMlogBean)?.let { data ->
                                RecentVideoBean(
                                    id = 0L,
                                    idStr = data.id,
                                    name = data.title,
                                    artist = data.creator.nickname,
                                    cover = data.coverUrl,
                                    duration = data.duration,
                                    tag = "MLOG"
                                )
                            }
                            else -> null
                        }
                    }
                    _uiState.value = uiState.value.copy(
                        videos = videoList,
                        videoState = NetworkStatus.Successful
                    )
                } else {
                    _uiState.value = uiState.value.copy(
                        videoState = NetworkStatus.Failed("The error code is ${response.data.code}")
                    )
                    _eventFlow.emit("The error code is ${response.data.code}")
                }
            }
            is RemoteResult.Error -> {
                _uiState.value = uiState.value.copy(
                    videoState = NetworkStatus.Failed(response.exception.message.toString())
                )
                _eventFlow.emit(response.exception.message.toString())
            }
        }
    }

    /**获取最近播放的播客*/
    private suspend fun getRecentDjs() {
        val response = baseApiCall { service.getRecentDjs(APP.cookie) }
        when (response) {
            is RemoteResult.Success -> {
                if (response.data.code == 200) {
                    _uiState.value = uiState.value.copy(
                        djs = response.data.data.list,
                        djState = NetworkStatus.Successful
                    )
                } else {
                    _uiState.value = uiState.value.copy(
                        djState = NetworkStatus.Failed("The error code is ${response.data.code}")
                    )
                    _eventFlow.emit("The error code is ${response.data.code}")
                }
            }
            is RemoteResult.Error -> {
                _uiState.value = uiState.value.copy(
                    djState = NetworkStatus.Failed(response.exception.message.toString())
                )
                _eventFlow.emit(response.exception.message.toString())
            }
        }
    }
}