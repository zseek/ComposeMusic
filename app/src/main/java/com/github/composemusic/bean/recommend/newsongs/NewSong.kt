package com.github.composemusic.bean.recommend.newsongs

import com.github.composemusic.bean.song.SongData

data class NewSong(
    val id: Long,
    val type: Int,
    val name: String,
    val copywriter: Any?,
    val picUrl: String,
    val canDislike: Boolean,
    val trackNumberUpdateTime: Any?,
    val song: SongData,
    val alg: String,
)