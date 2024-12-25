package com.github.composemusic.bean.song

data class BaseSong<T>(
    val code: Int,
    val privileges: Any,
    val songs: T
)