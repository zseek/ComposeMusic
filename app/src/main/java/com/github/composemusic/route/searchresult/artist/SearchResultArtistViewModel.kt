package com.github.composemusic.route.searchresult.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.composemusic.datapaging.creator
import com.github.composemusic.network.MusicApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchResultArtistViewModel @Inject constructor(private val service: MusicApiService) : ViewModel() {
    /**获取搜索结果中的歌手部分*/
    fun getSearchArtistResult(keywords: String) = creator { offset, limit ->
//        val response = service.getSearchArtistResult(keywords = keywords,offset = offset*limit,limit = limit)
        val response = service.getSearchArtistResult(keywords = keywords, offset = offset, limit = limit)
        response.result.artists
    }.flow.cachedIn(viewModelScope)
}