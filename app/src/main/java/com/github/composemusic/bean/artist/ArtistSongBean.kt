package com.github.composemusic.bean.artist

import com.github.composemusic.bean.song.Track

data class ArtistSongBean(
    val artist: Artist,
    val code: Int,
    val hotSongs: List<Track>,
    val more: Boolean
)