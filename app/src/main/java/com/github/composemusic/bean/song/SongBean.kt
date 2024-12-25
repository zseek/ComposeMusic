package com.github.composemusic.bean.song

import com.github.composemusic.bean.albums.AlbumsBean
import com.github.composemusic.bean.artist.Artist

data class SongBean(
    val id: Long,
    val name: String,
    val artists: List<Artist>,
    val album: AlbumsBean,
    val duration: Int,
    val copyrightId: Int,
    val status: Int,
    val alias: List<Any>,
    val rtype: Int,
    val ftype: Int,
    val mvid: Int,
    val fee: Int,
    val rUrl: Any,
    val mark: Long,
)