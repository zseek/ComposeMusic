package com.github.composemusic.bean.search

data class BaseSearch<T>(
    val code: Int,
    val result: T
)
