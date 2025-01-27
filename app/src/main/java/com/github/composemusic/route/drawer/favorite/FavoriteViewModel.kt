package com.github.composemusic.route.drawer.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.composemusic.APP
import com.github.composemusic.datapaging.creator
import com.github.composemusic.network.MusicApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val service: MusicApiService) : ViewModel() {

    /** 获取收藏的歌手列表 */
    fun getFavoriteArtists() = creator { offset, limit ->
//        val response = service.getFavoriteArtist(cookie = APP.cookie, offset = offset * limit, limit = limit)
        val response = service.getFavoriteArtist(cookie = APP.cookie, offset = offset, limit = limit)
        response.data
    }.flow.cachedIn(viewModelScope)

    /**获取收藏的Mv列表*/
    fun getFavoriteMvs() = creator { offset, limit ->
//        val response = service.getFavoriteMvs(cookie = APP.cookie, offset = offset * limit, limit = limit)
        val response = service.getFavoriteMvs(cookie = APP.cookie, offset = offset, limit = limit)
        response.data
    }.flow.cachedIn(viewModelScope)

    /**获取收藏的专辑列表*/
    fun getFavoriteAlbums() = creator { offset, limit ->
//        val response = service.getFavoriteAlbum(cookie = APP.cookie, offset = offset * limit, limit = limit)
        val response = service.getFavoriteAlbum(cookie = APP.cookie, offset = offset, limit = limit)
        response.data
    }.flow.cachedIn(viewModelScope)
}