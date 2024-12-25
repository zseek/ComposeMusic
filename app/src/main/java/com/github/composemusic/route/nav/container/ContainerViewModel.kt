package com.github.composemusic.route.nav.container

import android.app.UiModeManager
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.composemusic.APP
import com.github.composemusic.network.MusicApiService
import com.github.composemusic.parm.Constants
import com.github.composemusic.route.musicplayer.service.AudioPlayState
import com.github.composemusic.route.musicplayer.service.AudioPlayerEvent
import com.github.composemusic.route.musicplayer.service.MusicServiceHandler
import com.github.composemusic.tool.SharedPreferencesUtil
import com.github.composemusic.ui.theme.ThemeModeStatus
import com.github.composemusic.ui.theme.setCurrentThemeMode
import com.github.composemusic.ui.theme.themeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContainerViewModel @Inject constructor(
    private val service: MusicApiService,
    private val musicServiceHandler: MusicServiceHandler
) : ViewModel() {

    private val _userStatus = mutableStateOf(ContainerUIStatus())
    val userStatus: State<ContainerUIStatus> = _userStatus

    init {
        viewModelScope.launch {
            _userStatus.value = getAccount()
            isDarkMode()
            musicServiceHandler.eventFlow.collectLatest {
                when (it) {
                    is AudioPlayState.Playing -> {
                        _userStatus.value = userStatus.value.copy(isPlaying = it.isPlaying)
                    }

                    else -> {}
                }
            }
        }
    }

    /**判断系统所使用的模式是否为深色模式*/
    private fun isDarkMode() {
        val systemUIMode = APP.context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        if (systemUIMode.nightMode == UiModeManager.MODE_NIGHT_YES) {
            _userStatus.value = userStatus.value.copy(
                isDark = true
            )
        } else {
            _userStatus.value = userStatus.value.copy(
                isDark = false
            )
        }
    }

    fun onEvent(event: ContainerEvent) {
        viewModelScope.launch {
            when (event) {
                is ContainerEvent.ChangePlayStatus -> {
                    musicServiceHandler.onEvent(AudioPlayerEvent.PlayOrPause)
                }

                is ContainerEvent.Next -> {
                    musicServiceHandler.onEvent(AudioPlayerEvent.Next)
                }

                is ContainerEvent.Prior -> {
                    musicServiceHandler.onEvent(AudioPlayerEvent.Prior)
                }

                is ContainerEvent.Logout -> {
                    SharedPreferencesUtil.instance.putValue(APP.context, Constants.Cookie, "")
                    SharedPreferencesUtil.instance.putValue(APP.context, Constants.UserId, 0L)
                }

                is ContainerEvent.UIMode -> {
                    if (event.isDark) {
                        //开启深色模式
                        themeState.value = ThemeModeStatus.Dark.mode
                        setCurrentThemeMode(ThemeModeStatus.Dark.mode)
                    } else {
                        //关闭深色模式
                        themeState.value = ThemeModeStatus.Light.mode
                        setCurrentThemeMode(ThemeModeStatus.Light.mode)
                    }
                    _userStatus.value = userStatus.value.copy(
                        isDark = event.isDark
                    )
                }
            }
        }
    }

    private suspend fun getAccount(): ContainerUIStatus {
        val cookie =
            SharedPreferencesUtil.instance.getValue(APP.context, Constants.Cookie, "").toString()
        val response = service.getAccountInfo(cookie)
        Log.d("getAccount", "getAccount: ${response.profile.avatarUrl}")
        return ContainerUIStatus(response.profile.avatarUrl, response.profile.nickname)
    }

}