package com.github.composemusic.route.drawer.recent

import com.github.composemusic.bean.albums.AlbumsBean
import com.github.composemusic.bean.dj.DjRadioBean
import com.github.composemusic.bean.playlist.Playlist
import com.github.composemusic.bean.recent.RecentPlayBean
import com.github.composemusic.bean.recent.RecentVideoBean
import com.github.composemusic.bean.song.Track
import com.github.composemusic.route.playlist.NetworkStatus

data class RecentPlayUIState(
    val songs: List<RecentPlayBean<Track>> = emptyList(),
    val playlists: List<RecentPlayBean<Playlist>> = emptyList(),
    val albums: List<RecentPlayBean<AlbumsBean>> = emptyList(),
    val videos: List<RecentVideoBean> = emptyList(),
    val djs: List<RecentPlayBean<DjRadioBean>> = emptyList(),
    val songState: NetworkStatus = NetworkStatus.Waiting,
    val playlistState: NetworkStatus = NetworkStatus.Waiting,
    val albumState: NetworkStatus = NetworkStatus.Waiting,
    val videoState: NetworkStatus = NetworkStatus.Waiting,
    val djState: NetworkStatus = NetworkStatus.Waiting,
)