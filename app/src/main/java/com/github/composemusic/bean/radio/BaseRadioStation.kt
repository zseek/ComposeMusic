package com.github.composemusic.bean.radio

data class BaseRadioStation<T>(
    val code:Int,
    val djRadios:T,
    val hasMore:Boolean
)
