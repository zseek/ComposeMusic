package com.github.composemusic.route.nav.recommend.albums

import com.github.composemusic.bean.albums.AlbumsBean
import com.github.composemusic.bean.albums.DigitAlbumsBean

data class AlbumUIStatus(
    val albums: List<AlbumsBean> = emptyList(), // 最新专辑
    val digitAlbums: List<DigitAlbumsBean> = emptyList(), // 数字专辑
    val albumsRank: List<DigitAlbumsBean> = emptyList() // 专辑榜单
)
