package com.github.composemusic.route.nav.mine

import com.github.composemusic.bean.banner.BannerBean
import com.github.composemusic.bean.playlist.Playlist
import com.github.composemusic.route.playlist.NetworkStatus

data class MineUIStatus(
    var banners:List<BannerBean> = emptyList(),
    var creates:MutableList<Playlist> = mutableListOf(),
    var favorites:MutableList<Playlist> = mutableListOf(),
    var preferBean:Playlist? = null,
    var mapPlaylist:MutableMap<String,Boolean> = mutableMapOf(),
    val playlistState:NetworkStatus = NetworkStatus.Waiting
)
