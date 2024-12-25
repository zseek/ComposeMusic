package com.github.composemusic.usecase.song

import com.github.composemusic.room.song.MusicRepository

class UpdateSizeMusicCase(private val repository: MusicRepository) {
    suspend operator fun invoke(songID:Long,size:String) = repository.updateSize(songID, size)
}