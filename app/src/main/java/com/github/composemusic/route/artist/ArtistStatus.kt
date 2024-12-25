package com.github.composemusic.route.artist

import com.github.composemusic.bean.albums.AlbumsBean
import com.github.composemusic.bean.artist.Artist
import com.github.composemusic.bean.artist.ArtistInfoBean
import com.github.composemusic.bean.mv.MvInfoBean
import com.github.composemusic.bean.song.Track

data class ArtistUIStatus(
    val isFollow: Boolean = false,
    val artist: ArtistInfoBean? = null,
    val songs: List<Track> = emptyList(),
    val albums: List<AlbumsBean> = emptyList(),
    val mvs: List<MvInfoBean> = emptyList(),
    val similar: List<Artist> = emptyList()
)

sealed class ArtistStatus {
    data class NetworkFailed(val msg: String) : ArtistStatus()
}