package com.github.composemusic.bean.search

import com.github.composemusic.bean.albums.AlbumsBean
import com.github.composemusic.bean.artist.Artist
import com.github.composemusic.bean.playlist.Playlist
import com.github.composemusic.bean.song.SongBean

data class SearchSuggestionBean(
    val albums: List<AlbumsBean>,
    val artists: List<Artist>,
    val songs: List<SongBean>,
    val playlists: List<Playlist>,
    val order: List<String>
)
