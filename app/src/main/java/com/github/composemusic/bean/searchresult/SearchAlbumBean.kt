package com.github.composemusic.bean.searchresult

import com.github.composemusic.bean.albums.AlbumsBean

data class SearchAlbumBean(
    val albumCount: Int,
    val albums: List<AlbumsBean>
)