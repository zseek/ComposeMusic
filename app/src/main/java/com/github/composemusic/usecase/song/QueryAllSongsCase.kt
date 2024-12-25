package com.github.composemusic.usecase.song

import com.github.composemusic.bean.song.SongMediaBean
import com.github.composemusic.room.song.MusicRepository

class QueryAllSongsCase(private val repository: MusicRepository) {
    suspend operator fun invoke():List<SongMediaBean>{
        return repository.queryAllSongs().sortedBy { it.createTime }
    }
}