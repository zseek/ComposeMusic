package com.github.composemusic.route.searchresult.mv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.composemusic.datapaging.creator
import com.github.composemusic.network.MusicApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchResultMvViewModel @Inject constructor(private val service: MusicApiService):ViewModel() {
    /**获取搜索结果中的Mv部分*/
    fun getSearchMvResult(keywords:String) = creator { offset, limit ->
//        val response = service.getSearchMvResult(keywords = keywords,offset = offset*limit,limit = limit)
        val response = service.getSearchMvResult(keywords = keywords,offset = offset,limit = limit)
        response.result.mvs
    }.flow.cachedIn(viewModelScope)
}