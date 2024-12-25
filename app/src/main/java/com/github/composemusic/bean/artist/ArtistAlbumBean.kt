package com.github.composemusic.bean.artist

import com.github.composemusic.bean.albums.AlbumsBean

data class ArtistAlbumBean(
    val artist: Artist,
    val code: Int,
    val hotAlbums: List<AlbumsBean>,
    val more: Boolean
)