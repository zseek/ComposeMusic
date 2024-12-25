package com.github.composemusic.bean.searchresult

import com.github.composemusic.bean.artist.Artist

data class SearchArtistBean(
    val artistCount: Int,
    val artists: List<Artist>,
    val searchQcReminder: Any
)