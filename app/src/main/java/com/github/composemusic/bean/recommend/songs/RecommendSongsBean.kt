package com.github.composemusic.bean.recommend.songs

import com.github.composemusic.bean.song.Track

// /recommend/songs
data class RecommendSongsBean(
    val dailySongs: List<Track>,
    val orderSongs: List<Any>, // 内部未空
    val recommendReasons: List<Any>, // Any存在内容, 暂未完成
    val mvResourceInfos: Any?,
    val demote: Boolean,
)