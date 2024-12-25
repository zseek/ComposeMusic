package com.github.composemusic.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.composemusic.bean.download.DownloadMusicBean
import com.github.composemusic.bean.search.SearchRecordBean
import com.github.composemusic.bean.song.SongMediaBean
import com.github.composemusic.room.download.DownloadDao
import com.github.composemusic.room.search.SearchHistoryDao
import com.github.composemusic.room.song.MusicDao

@Database(
    entities = [SearchRecordBean::class, SongMediaBean::class, DownloadMusicBean::class],
    version = 11,
    exportSchema = false
)
abstract class ComposeMusicDataBase : RoomDatabase() {
    abstract val searchHistoryDao: SearchHistoryDao
    abstract val musicDao: MusicDao
    abstract val downloadDao: DownloadDao

    companion object {
        const val DATABASE_NAME = "ComposeMusic"
    }
}