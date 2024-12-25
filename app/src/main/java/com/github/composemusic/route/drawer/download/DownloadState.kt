package com.github.composemusic.route.drawer.download

import com.github.composemusic.bean.download.DownloadMusicBean

data class DownloadDialogState(
    val isVisibility: Boolean = false,
    val title: String = "系统提示",
    val content: String = "您想清除所有下载记录吗?",
    val confirmBtn: String = "确认",
    val cancelBtn: String = "取消"
)

sealed class DownloadEvent {
    data class PlayLocalMusic(val bean: DownloadMusicBean) : DownloadEvent()
    data class PlayOrPause(val bean: DownloadMusicBean) : DownloadEvent()
    object ShowDialog : DownloadEvent()
    object DialogConfirm : DownloadEvent()
    object DialogCancel : DownloadEvent()
}