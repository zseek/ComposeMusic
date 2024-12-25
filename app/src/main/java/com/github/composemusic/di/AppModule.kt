package com.github.composemusic.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import androidx.room.Room
import com.github.composemusic.APP
import com.github.composemusic.network.MusicApiService
import com.github.composemusic.room.ComposeMusicDataBase
import com.github.composemusic.room.download.DownloadRepository
import com.github.composemusic.room.download.DownloadRepositoryImpl
import com.github.composemusic.room.search.SearchHistoryRepository
import com.github.composemusic.room.search.SearchHistoryRepositoryImpl
import com.github.composemusic.room.song.MusicRepository
import com.github.composemusic.room.song.MusicRepositoryImpl
import com.github.composemusic.route.drawer.download.service.DownloadHandler
import com.github.composemusic.route.musicplayer.notification.MusicNotificationManager
import com.github.composemusic.route.musicplayer.service.MusicServiceHandler
import com.github.composemusic.usecase.download.DeleteAllDownloadCase
import com.github.composemusic.usecase.download.DeleteDownloadCase
import com.github.composemusic.usecase.download.DownloadUseCase
import com.github.composemusic.usecase.download.InsertAllDownloadCase
import com.github.composemusic.usecase.download.InsertDownloadCase
import com.github.composemusic.usecase.download.QueryAllDownloadCase
import com.github.composemusic.usecase.download.QueryAllDownloadImmediateCase
import com.github.composemusic.usecase.download.UpdateDownloadStateCase
import com.github.composemusic.usecase.download.UpdateDownloadTaskIDCase
import com.github.composemusic.usecase.search.DeleteAllCase
import com.github.composemusic.usecase.search.InsertAllCase
import com.github.composemusic.usecase.search.InsertCase
import com.github.composemusic.usecase.search.QueryAllCase
import com.github.composemusic.usecase.search.SearchUseCase
import com.github.composemusic.usecase.song.DeleteAllMusicCase
import com.github.composemusic.usecase.song.DeleteMusicCase
import com.github.composemusic.usecase.song.InsertAllMusicCase
import com.github.composemusic.usecase.song.InsertMusicCase
import com.github.composemusic.usecase.song.MusicUseCase
import com.github.composemusic.usecase.song.QueryAllMusicCase
import com.github.composemusic.usecase.song.QueryAllSongsCase
import com.github.composemusic.usecase.song.UpdateDurationMusicCase
import com.github.composemusic.usecase.song.UpdateLoadingMusicCase
import com.github.composemusic.usecase.song.UpdateSizeMusicCase
import com.github.composemusic.usecase.song.UpdateURLMusicCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASEURL = "http://117.88.62.141:3999/"
    @Singleton
    @Provides
    fun provideClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(3000L, TimeUnit.MILLISECONDS)
            .writeTimeout(3000L, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASEURL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideMusicService(retrofit: Retrofit): MusicApiService {
        return retrofit.create(MusicApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideDataBase(): ComposeMusicDataBase {
        return Room.databaseBuilder(
            context = APP.context,
            klass = ComposeMusicDataBase::class.java,
            name = ComposeMusicDataBase.DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideSearchHistoryRepository(dataBase: ComposeMusicDataBase): SearchHistoryRepository =
        SearchHistoryRepositoryImpl(dataBase.searchHistoryDao)

    @Singleton
    @Provides
    fun provideSearchHistoryUseCase(repository: SearchHistoryRepository): SearchUseCase =
        SearchUseCase(
            queryAll = QueryAllCase(repository),
            insert = InsertCase(repository),
            deleteAll = DeleteAllCase(repository),
            insertAll = InsertAllCase(repository)
        )

    @Singleton
    @Provides
    fun provideMusicRepository(dataBase: ComposeMusicDataBase): MusicRepository =
        MusicRepositoryImpl(dataBase.musicDao)

    @Singleton
    @Provides
    fun provideMusicUseCase(repository: MusicRepository): MusicUseCase =
        MusicUseCase(
            queryAll = QueryAllMusicCase(repository),
            insert = InsertMusicCase(repository),
            deleteAll = DeleteAllMusicCase(repository),
            insertAll = InsertAllMusicCase(repository),
            updateUrl = UpdateURLMusicCase(repository),
            updateLoading = UpdateLoadingMusicCase(repository),
            queryAllSongsCase = QueryAllSongsCase(repository),
            updateDuration = UpdateDurationMusicCase(repository),
            deleteSong = DeleteMusicCase(repository),
            updateSize = UpdateSizeMusicCase(repository)
        )

    @Singleton
    @Provides
    fun provideDownloadRepository(dataBase: ComposeMusicDataBase): DownloadRepository =
        DownloadRepositoryImpl(dataBase.downloadDao)

    @Singleton
    @Provides
    fun provideDownloadUseCase(repository: DownloadRepository): DownloadUseCase =
        DownloadUseCase(
            queryAllImmediate = QueryAllDownloadImmediateCase(repository),
            queryAll = QueryAllDownloadCase(repository),
            insertAll = InsertAllDownloadCase(repository),
            insert = InsertDownloadCase(repository),
            delete = DeleteDownloadCase(repository),
            deleteAll = DeleteAllDownloadCase(repository),
            updateTaskID = UpdateDownloadTaskIDCase(repository),
            updateDownloadState = UpdateDownloadStateCase(repository)
        )

    @Singleton
    @Provides
    fun provideAudioAttributes(): AudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    @OptIn(UnstableApi::class)
    @Singleton
    @Provides
    fun provideMusicExoPlayer(@ApplicationContext context: Context, audioAttributes: AudioAttributes): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setTrackSelector(DefaultTrackSelector(context))
            .build()
    }


    @Provides
    @Singleton
    fun provideMediaSession(
        @ApplicationContext context: Context,
        player: ExoPlayer,
    ): MediaSession = MediaSession.Builder(context, player).build()

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context,
        player: ExoPlayer,
    ): MusicNotificationManager = MusicNotificationManager(
        context = context,
        exoPlayer = player
    )

    @Provides
    @Singleton
    fun provideServiceHandler(exoPlayer: ExoPlayer, musicUseCase: MusicUseCase, service: MusicApiService
    ): MusicServiceHandler = MusicServiceHandler(
        exoPlayer = exoPlayer,
        musicUseCase = musicUseCase,
        service = service
    )

    @Provides
    @Singleton
    fun provideDownloadHandler(useCase: DownloadUseCase): DownloadHandler = DownloadHandler(useCase)
}