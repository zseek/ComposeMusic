package com.github.composemusic.route.nav.recommend.songs

import com.github.composemusic.bean.song.Track
import com.github.composemusic.bean.recommend.newsongs.NewSong

data class SongUIStatus(
    val newSongs: List<NewSong> = emptyList(),
    val daySongs: List<Track> = emptyList()
)
