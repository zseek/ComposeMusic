package com.github.composemusic.usecase.song

import com.github.composemusic.bean.song.SongMediaBean
import com.github.composemusic.room.song.MusicRepository

class DeleteMusicCase(private val repository: MusicRepository) {
    suspend operator fun invoke(bean: SongMediaBean) = repository.deleteSong(bean)
}