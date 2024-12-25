package com.github.composemusic.bean.albums

data class BaseAlbums<T> (
    val code:Int,
    val albums:T
)