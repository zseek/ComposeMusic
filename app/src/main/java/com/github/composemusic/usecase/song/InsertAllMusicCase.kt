package com.github.composemusic.usecase.song

import com.github.composemusic.bean.song.SongMediaBean
import com.github.composemusic.room.song.MusicRepository

class InsertAllMusicCase(private val repository: MusicRepository) {
    suspend operator fun invoke(bean: List<SongMediaBean>) = repository.insertAll(bean)
}