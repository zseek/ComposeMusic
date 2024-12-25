package com.github.composemusic.route.nav.recommend.songs

import com.github.composemusic.bean.recommend.newsongs.NewSong
import com.github.composemusic.bean.song.Track

sealed class SongEvent {
    data class InsertDaySong(val bean: Track) : SongEvent()
    data class InsertNewSong(val bean: NewSong) : SongEvent()
}