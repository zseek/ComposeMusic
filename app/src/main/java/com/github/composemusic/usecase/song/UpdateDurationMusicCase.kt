package com.github.composemusic.usecase.song

import com.github.composemusic.room.song.MusicRepository

class UpdateDurationMusicCase(private val repository: MusicRepository) {
    suspend operator fun invoke(songID:Long,duration:Long) = repository.updateDuration(songID, duration)
}