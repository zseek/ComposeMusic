package com.github.composemusic.route.drawer.user

import android.util.Log
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
class UserProfileViewModel @Inject constructor(
    private val service: MusicApiService
) : ViewModel() {
    private val _uiStatus = mutableStateOf(UserProfileUIStatus())
    val uiStatus: State<UserProfileUIStatus> = _uiStatus

    private val _eventFlow = MutableSharedFlow<UserProfileStatus>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            getUserDetailInfo(APP.userId)
        }
    }

    /**对异常情况进行处理了的网络请求, 获取用户详细信息*/
    private suspend fun getUserDetailInfo(userId: Long) {
        val response = baseApiCall { service.getUserDetailInfo(userId) }
        when (response) {
            is RemoteResult.Success -> {
                Log.d("getUserDetailInfo", response.data.toString())
                _uiStatus.value = uiStatus.value.copy(
                    profile = response.data
                )
            }
            is RemoteResult.Error -> {
                _eventFlow.emit(
                    UserProfileStatus.NetworkFailed(
                        response.exception.message.toString()
                    )
                )
            }
        }
    }
}