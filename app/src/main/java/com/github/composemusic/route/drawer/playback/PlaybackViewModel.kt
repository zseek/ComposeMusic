package com.github.composemusic.route.drawer.playback

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arialyy.aria.util.CommonUtil
import com.github.composemusic.APP
import com.github.composemusic.bean.download.DownloadMusicBean
import com.github.composemusic.bean.song.SongMediaBean
import com.github.composemusic.bean.song.SongUrlBean
import com.github.composemusic.network.MusicApiService
import com.github.composemusic.network.RemoteResult
import com.github.composemusic.network.baseApiCall
import com.github.composemusic.route.drawer.download.service.DownloadHandler
import com.github.composemusic.route.musicplayer.service.AudioPlayerEvent
import com.github.composemusic.route.musicplayer.service.MusicServiceHandler
import com.github.composemusic.ui.theme.grey
import com.github.composemusic.usecase.song.MusicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val musicService: MusicApiService,
    private val musicUseCase: MusicUseCase,
    private val musicServiceHandler: MusicServiceHandler,
    private val downloadHandler: DownloadHandler
) : ViewModel() {
    val playlist = mutableStateListOf<SongMediaBean>()

    private val _uiState = mutableStateOf(PlayBackUIState())
    val uiState: State<PlayBackUIState> = _uiState

    private val _eventFlow = MutableSharedFlow<PlayBackState>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var deleteSong: SongMediaBean? = null

    private var job: Job? = null

    init {
        getPlaylist()
    }

    fun onEvent(event: PlayBackEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (event) {
                is PlayBackEvent.Download -> {
                    val path = downloadHandler.createDownloadFolder()
                    getDownloadURL(event.bean.songID) { bean ->
                        val downloadBean = DownloadMusicBean(
                            musicID = event.bean.songID,
                            taskID = -1L,
                            musicName = event.bean.songName,
                            artist = event.bean.artist,
                            cover = event.bean.cover,
                            url = bean.url,
                            path = path + File.separator + event.bean.songName + ".mp3",
                            size = CommonUtil.formatFileSize(bean.size.toDouble()),
                            download = false,
                            progress = 0f,
                            speed = "waiting",
                            progressColor = grey
                        )
                        downloadHandler.download(downloadBean)
                        _eventFlow.emit(PlayBackState.Message(" ${event.bean.songName} 已经添加到下载队列中!"))
                    }
                }

                is PlayBackEvent.DeleteItem -> {
                    deleteSong = event.bean
                    _uiState.value = uiState.value.copy(
                        content = "您正在删除${event.bean.songName},请确认是否执行!",
                        isVisibility = true
                    )
                }

                is PlayBackEvent.ConfirmDelete -> {
                    if (deleteSong != null) {
                        musicUseCase.deleteSong(deleteSong!!)
                        musicServiceHandler.onEvent(AudioPlayerEvent.DeletePlayItem(deleteSong!!))
                        _uiState.value = uiState.value.copy(
                            isVisibility = false
                        )
                        _eventFlow.emit(PlayBackState.Message("成功删除 ${deleteSong!!.songName}"))
                    }
                    deleteSong = null
                }

                is PlayBackEvent.CancelDelete -> {
                    deleteSong = null
                    _uiState.value = uiState.value.copy(
                        isVisibility = false
                    )
                }

                is PlayBackEvent.PlayItem -> {
                    musicServiceHandler.onEvent(AudioPlayerEvent.Group(0L))
                }

                is PlayBackEvent.ApplicationPermission -> {

                }
            }
        }
    }

    private fun getPlaylist() {
        job?.cancel()
        job = musicUseCase.queryAll().onEach {
            playlist.addAll(it)
        }.launchIn(viewModelScope)
    }

    /**获取下载音乐的URL链接, 需要VIP下载权限的歌曲若登录用户非VIP会返回空的URL */
    private suspend fun getDownloadURL(id: Long, onUrlResponse: suspend (SongUrlBean) -> Unit) {
        val response = baseApiCall { musicService.getDownloadURL(id = id, cookie = APP.cookie) }
        when (response) {
            is RemoteResult.Success -> {
                if (response.data.code == 200) {
                    onUrlResponse(response.data.data)
                }
            }
            is RemoteResult.Error -> {
                _eventFlow.emit(PlayBackState.Message(response.exception.message.toString()))
            }
        }
    }
}