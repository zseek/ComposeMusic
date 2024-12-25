package com.github.composemusic.bean.albums

import com.github.composemusic.bean.song.Track

data class AlbumDetailBean(
    val album: AlbumsBean,
    val code: Int,
    val resourceState: Boolean,
    val songs: List<Track>
)