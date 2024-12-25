package com.github.composemusic.bean.albums

data class BaseProduct<T> (
    val code:Int,
    val products:T
)