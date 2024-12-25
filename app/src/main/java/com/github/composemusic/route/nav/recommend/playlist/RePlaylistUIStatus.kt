package com.github.composemusic.route.nav.recommend.playlist

import com.github.composemusic.bean.recommend.playlist.Result

data class RePlaylistUIStatus(
    val newPlaylist:List<Result> = emptyList(),
    val dayPlaylist:List<Recommend> = emptyList(),
)
