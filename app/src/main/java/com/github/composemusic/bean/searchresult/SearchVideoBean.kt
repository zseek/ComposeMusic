package com.github.composemusic.bean.searchresult

import com.github.composemusic.bean.video.VideoBean

data class SearchVideoBean(
    val searchQcReminder: Any,
    val videoCount: Int,
    val videos: List<VideoBean>
)