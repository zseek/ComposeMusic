package com.github.composemusic.usecase.song

import com.github.composemusic.room.song.MusicRepository

class UpdateURLMusicCase(private val repository: MusicRepository) {
     suspend operator fun invoke(songID:Long,url:String) = repository.updateURL(songID, url)
}