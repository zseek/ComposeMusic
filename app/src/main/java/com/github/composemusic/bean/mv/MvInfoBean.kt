package com.github.composemusic.bean.mv

import com.github.composemusic.bean.artist.Artist

data class MvInfoBean(
    val artist: Artist,
    val artistName: String,
    val duration: Int,
    val id: Long,
    val imgurl: String,
    val imgurl16v9: String,
    val name: String,
    val playCount: Long,
    val publishTime: String,
    val status: Int,
    val subed: Boolean
)