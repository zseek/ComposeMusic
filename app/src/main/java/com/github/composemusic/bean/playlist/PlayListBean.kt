package com.github.composemusic.bean.playlist

data class PlayListBean(
    val code: Int,
    val more: Boolean,
    val playlist: List<Playlist>,
    val version: String
)