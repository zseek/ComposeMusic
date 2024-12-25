package com.github.composemusic.bean.recommend.newsongs

data class RecommendNewSongsBean(
    val category: Int,
    val code: Int,
    val result: List<NewSong>
)