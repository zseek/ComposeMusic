package com.github.composemusic.route.artist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.composemusic.APP
import com.github.composemusic.bean.song.SongMediaBean
import com.github.composemusic.bean.song.Track
import com.github.composemusic.network.MusicApiService
import com.github.composemusic.network.RemoteResult
import com.github.composemusic.network.baseApiCall
import com.github.composemusic.parm.Constants
import com.github.composemusic.route.musicplayer.service.MusicServiceHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val service: MusicApiService,
    private val musicServiceHandler: MusicServiceHandler
) : ViewModel() {
    private val _uiStatus = mutableStateOf(ArtistUIStatus())
    val uiStatus: State<ArtistUIStatus> = _uiStatus

    private val _eventFlow = MutableSharedFlow<ArtistStatus>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            savedStateHandle.get<Long>(Constants.ArtistID)?.let {
                if (it != 0L) {
                    getArtistInfo(it)
                    getArtistSongs(it)
                    getArtistMvs(it)
                    getSimilarArtists(it)
                }
            }
        }
    }

    /**播放歌曲*/
    fun playSong(song: Track) {
        viewModelScope.launch {
            musicServiceHandler.isExistPlaylist(
                SongMediaBean(
                    createTime = System.currentTimeMillis(),
                    songID = song.id,
                    songName = song.name,
                    cover = song.al.picUrl,
                    artist = song.ar[0].name,
                    url = "",
                    isLoading = false,
                    duration = 0L,
                    size = ""
                )
            )
        }
    }

    /**获取歌手简要信息*/
    private suspend fun getArtistInfo(id: Long) {
        val response = baseApiCall { service.getArtistDetailInfo(id) }
        when (response) {
            is RemoteResult.Success -> {
                _uiStatus.value = uiStatus.value.copy(
                    artist = response.data.data.artist,
                    isFollow = response.data.data.user.followed
                )
                getArtistAlbums(id, response.data.data.artist.albumSize)
            }
            is RemoteResult.Error -> {
                _eventFlow.emit(ArtistStatus.NetworkFailed(response.exception.message.toString()))
            }
        }
    }

    /**获取歌手热门50首歌曲*/
    private suspend fun getArtistSongs(id: Long) {
        val response = baseApiCall { service.getArtistSongs(id) }
        when (response) {
            is RemoteResult.Success -> {
                _uiStatus.value = uiStatus.value.copy(songs = response.data.hotSongs)
            }
            is RemoteResult.Error -> {
                _eventFlow.emit(ArtistStatus.NetworkFailed(response.exception.message.toString()))
            }
        }
    }

    /**获取歌手专辑*/
    private suspend fun getArtistAlbums(id: Long, limit: Int) {
        val response = baseApiCall { service.getArtistAlbums(id = id, limit = limit) }
        when (response) {
            is RemoteResult.Success -> {
                _uiStatus.value = uiStatus.value.copy(albums = response.data.hotAlbums)
            }
            is RemoteResult.Error -> {
                _eventFlow.emit(ArtistStatus.NetworkFailed(response.exception.message.toString()))
            }
        }
    }

    /**获取歌手MV*/
    private suspend fun getArtistMvs(id: Long) {
        val response = baseApiCall { service.getArtistMvs(id) }
        when (response) {
            is RemoteResult.Success -> {
                _uiStatus.value = uiStatus.value.copy(mvs = response.data.mvs)
            }
            is RemoteResult.Error -> {
                _eventFlow.emit(ArtistStatus.NetworkFailed(response.exception.message.toString()))
            }
        }
    }

    /**获取相似歌手*/
    private suspend fun getSimilarArtists(id: Long) {
        val response = baseApiCall { service.getSimilarArtist(id, cookie = APP.cookie)}
        when (response) {
            is RemoteResult.Success -> {
                _uiStatus.value = uiStatus.value.copy(similar = response.data.artists)
            }
            is RemoteResult.Error -> {
                _eventFlow.emit(ArtistStatus.NetworkFailed(response.exception.message.toString()))
            }
        }
    }

}