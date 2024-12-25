package com.github.composemusic.route.nav.radiostation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.composemusic.APP
import com.github.composemusic.bean.radio.RecommendRadioBean
import com.github.composemusic.bean.radio.program.ProgramRankBean
import com.github.composemusic.bean.song.SongMediaBean
import com.github.composemusic.datapaging.creator
import com.github.composemusic.network.MusicApiService
import com.github.composemusic.network.RemoteResult
import com.github.composemusic.network.baseApiCall
import com.github.composemusic.route.musicplayer.service.MusicServiceHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadioStationViewModel @Inject constructor(
    private val service: MusicApiService,
    private val musicServiceHandler: MusicServiceHandler
):ViewModel()  {

    val recommends = mutableStateListOf<RecommendRadioBean>()
    val hots = mutableStateListOf<RecommendRadioBean>()

    private val _eventFlow = MutableSharedFlow<String>() // 用于发射事件通知
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getRecommendRadioStation()
            getHotRadioStation()
        }
    }

    fun playProgram(bean: ProgramRankBean){
        viewModelScope.launch {
            musicServiceHandler.isExistPlaylist(
                SongMediaBean(
                    createTime = System.currentTimeMillis(),
                    songID = bean.program.id,
                    songName = bean.program.name,
                    cover = bean.program.blurCoverUrl,
                    artist = bean.program.dj.nickname,
                    url = "",
                    isLoading = false,
                    duration = 0L,
                    size = ""
                )
            )
        }
    }

    /**电台个性推荐列表(/dj/personalize/recommend)*/
    private suspend fun getRecommendRadioStation(){
        val response = baseApiCall { service.getRecommendRadioStation(APP.cookie) }
        when(response){
            is RemoteResult.Success->{
                if (response.data.code == 200){
                    recommends.addAll(response.data.data)
                }else{
                    _eventFlow.emit("The error code is ${response.data.code}")
                }
            }

            is RemoteResult.Error->{
                _eventFlow.emit(response.exception.message.toString())
            }
        }
    }

    /**热门电台(/dj/hot)*/
    private suspend fun getHotRadioStation(){
        val response = baseApiCall { service.getHotRadioStation(cookie = APP.cookie) }
        when(response){
            is RemoteResult.Success->{
                if (response.data.code == 200){
                    hots.addAll(response.data.djRadios)
                }else{
                    _eventFlow.emit("The error code is ${response.data.code}")
                }
            }

            is RemoteResult.Error->{
                _eventFlow.emit(response.exception.message.toString())
            }
        }
    }

    /**电台节目榜(/dj/program/toplist), 里面item为节目-歌曲*/
    fun getProgramRanking() = creator { offset, limit ->
//        val response = service.getProgramRanking(offset = offset*limit,limit = limit)
        val response = service.getProgramRanking(cookie = APP.cookie, offset = offset,limit = limit)
        response.toplist
    }.flow.cachedIn(viewModelScope) // 将 Flow 结果缓存到 ViewModel 的生命周期中

    /**新晋电台榜(/dj/toplist?type=new), 里面item为电台-歌单*/
    fun getNewProgramRanking() = creator { offset, limit ->
//        val response = service.getNewHotProgramRanking(type = "new",offset = offset*limit,limit = limit)
        val response = service.getNewHotProgramRanking(cookie = APP.cookie, type = "new",offset = offset,limit = limit)
        response.toplist
    }.flow.cachedIn(viewModelScope) // 将 Flow 结果缓存到 ViewModel 的生命周期中

    /**热门电台榜(/dj/toplist?type=hot), 里面item为电台-歌单*/
    fun getHotProgramRanking() = creator { offset, limit ->
//        val response = service.getNewHotProgramRanking(type = "hot",offset = offset*limit,limit = limit)
        val response = service.getNewHotProgramRanking(cookie = APP.cookie, type = "hot",offset = offset,limit = limit)
        response.toplist
    }.flow.cachedIn(viewModelScope) // 将 Flow 结果缓存到 ViewModel 的生命周期中
}