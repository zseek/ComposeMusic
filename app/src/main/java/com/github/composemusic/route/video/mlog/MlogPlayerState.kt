package com.github.composemusic.route.video.mlog

import com.github.composemusic.bean.video.MlogInfoBean

data class MlogPlayerUIState(
    val id: String = "",
    val videoId: String = "",
    val shareURL: String = "",
    val isFavorite: Boolean = false,
    val isPlaying: Boolean = false,
    val isFullScreen: Boolean = false,
    val isShowControl: Boolean = false,
    val progress: Float = 0f,
    val currentPosition: Long = 0L,
    val mlogInfo: MlogInfoBean? = null
)

sealed class MlogPlayerState {
    data class Message(val msg: String) : MlogPlayerState()
    object OpenComment : MlogPlayerState()
}

sealed class MlogPlayerEvent {
    data class SlidProgress(val progress: Float) : MlogPlayerEvent()
    object PlayOrPause : MlogPlayerEvent()
    object FullScreenControl : MlogPlayerEvent()
    object Favorite : MlogPlayerEvent()
    object Share : MlogPlayerEvent()
    object FullScreen : MlogPlayerEvent()
}