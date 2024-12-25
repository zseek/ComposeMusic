package com.github.composemusic.usecase.song

import com.github.composemusic.room.song.MusicRepository

class UpdateLoadingMusicCase(private val repository: MusicRepository) {
    suspend operator fun invoke(songID:Long,isLoading:Boolean) = repository.updateLoadingStatus(songID, isLoading)
}