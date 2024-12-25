package com.github.composemusic.usecase.song

import com.github.composemusic.bean.song.SongMediaBean
import com.github.composemusic.room.song.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QueryAllMusicCase(private val repository: MusicRepository) {
    operator fun invoke(): Flow<List<SongMediaBean>> {
        return repository.queryAll().map { bean ->
            //升序，按照创建时间从小到大排序，则先创建的在前面
            bean.sortedBy { it.createTime }
        }
    }
}