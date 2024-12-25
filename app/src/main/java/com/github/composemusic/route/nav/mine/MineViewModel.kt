package com.github.composemusic.route.nav.mine

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.composemusic.APP
import com.github.composemusic.network.MusicApiService
import com.github.composemusic.network.RemoteResult
import com.github.composemusic.network.baseApiCall
import com.github.composemusic.parm.Constants
import com.github.composemusic.route.playlist.NetworkStatus
import com.github.composemusic.tool.SharedPreferencesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(private val service: MusicApiService) : ViewModel() {

    private val _uiStatus = mutableStateOf(MineUIStatus())
    val uiStatus: State<MineUIStatus> = _uiStatus

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val loginMode = SharedPreferencesUtil.instance.getValue(APP.context, Constants.LoginMode, -1) as Int
            if (loginMode == Constants.QRCodeLoginMode) {
                APP.userId = getAccountInfo()
            }
            getPlaylist(APP.userId)
        }
    }

    //获取用户歌单，包括用户喜欢的歌曲、用户创建的歌单、用户收藏的歌单
    private suspend fun getPlaylist(uid: Long) {
        val response = baseApiCall { service.getPlayList(uid, APP.cookie) }
        when (response) {
            is RemoteResult.Success -> {
                if (response.data.code == 200 && response.data.playlist.isNotEmpty()) {
                    response.data.playlist.forEachIndexed { index, playlist ->
                        if (index == 0) {
                            //用户喜欢的歌曲
                            _uiStatus.value = uiStatus.value.copy(preferBean = playlist)
                            playlist?.let { _uiStatus.value.mapPlaylist += Constants.Preference to true }
                        } else if (playlist.userId == APP.userId) {
                            //用户创建的歌单，将登录用户ID和创建歌单的用户ID做对比，判断是否为用户所创建的歌单
                            _uiStatus.value.creates.add(playlist)
                            playlist?.let { _uiStatus.value.mapPlaylist += Constants.Create to true }
                        } else {
                            //用户收藏的歌单
                            _uiStatus.value.favorites.add(playlist)
                            playlist?.let { _uiStatus.value.mapPlaylist += Constants.Favorite to true }
                        }
                    }
                    _uiStatus.value = uiStatus.value.copy(
                        playlistState = NetworkStatus.Successful
                    )
                } else {
                    _uiStatus.value = uiStatus.value.copy(
                        playlistState = NetworkStatus.Failed("The error code is ${response.data.code}")
                    )
                    _eventFlow.emit("The error code is ${response.data.code}")
                }
            }

            is RemoteResult.Error -> {
                _uiStatus.value = uiStatus.value.copy(
                    playlistState = NetworkStatus.Failed(response.exception.message.toString())
                )
                _eventFlow.emit(response.exception.message.toString())
            }
        }
    }

    private suspend fun getAccountInfo(): Long {
        val cookie = SharedPreferencesUtil.instance.getValue(APP.context, Constants.Cookie, "").toString()
        val response = service.getAccountInfo(cookie)
        Log.d("getAccount", "getAccount: ${response.account.id}")
        SharedPreferencesUtil.instance.putValue(APP.context, Constants.ConsumerID, response.account.id)
        return response.account.id
    }
}