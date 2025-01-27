package com.github.composemusic.bean.video

data class VideoBean(
    val alg: Any,
    val aliaName: Any,
    val coverUrl: String,
    val creator: List<VideoCreate>,
    val durationms: Int,
    val markTypes: Any,
    val playTime: Long,
    val title: String,
    val transName: Any,
    val type: Int,
    val vid: String
)