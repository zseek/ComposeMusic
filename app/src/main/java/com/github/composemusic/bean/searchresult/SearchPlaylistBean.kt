package com.github.composemusic.bean.searchresult

import com.github.composemusic.bean.playlist.Playlist

data class SearchPlaylistBean(
    val searchQcReminder: Any,
    val playlists: List<Playlist>,
    val playlistCount: Int
)


