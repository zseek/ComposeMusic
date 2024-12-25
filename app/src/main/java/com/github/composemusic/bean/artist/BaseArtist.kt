package com.github.composemusic.bean.artist

data class BaseArtist(
    val code: Int,
    val more: Boolean,
    val artists: List<Artist>
)
