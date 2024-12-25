package com.github.composemusic.route.nav.recommend.artist

import com.github.composemusic.bean.artist.Artist

data class ArtistUIStatus(
    val artists: MutableList<Artist> = mutableListOf(),
    val isShowFilter: Boolean = false
)
