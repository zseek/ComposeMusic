package com.github.composemusic.usecase.song

import com.github.composemusic.room.song.MusicRepository

class DeleteAllMusicCase(private val repository: MusicRepository) {
    suspend operator fun invoke() = repository.deleteAll()
}