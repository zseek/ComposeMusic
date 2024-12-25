package com.github.composemusic.bean.searchresult

import com.github.composemusic.bean.song.Track

data class SearchSongBean(
    val searchQcReminder: Any,
    val songCount: Int,
    val songs: List<Track>
)