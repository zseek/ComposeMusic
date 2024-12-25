package com.github.composemusic.route.drawer.playback

import com.github.composemusic.bean.song.SongMediaBean

data class PlayBackUIState(
    val isVisibility: Boolean = false,
    val title: String = "系统提示",
    val content: String = "",
    val confirmBtn: String = "确认",
    val cancelBtn: String = "取消"
)

sealed class PlayBackState {
    data class Message(val msg: String) : PlayBackState()
    data class Authorized(val msg: String) : PlayBackState()
    object Permission : PlayBackState()
}

sealed class PlayBackEvent {
    data class Download(val bean: SongMediaBean) : PlayBackEvent()
    data class DeleteItem(val bean: SongMediaBean) : PlayBackEvent()
    data class PlayItem(val index: Int) : PlayBackEvent()
    object ConfirmDelete : PlayBackEvent()
    object CancelDelete : PlayBackEvent()
    object ApplicationPermission : PlayBackEvent()
}