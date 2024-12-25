package com.github.composemusic.route.searchresult.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.composemusic.datapaging.creator
import com.github.composemusic.network.MusicApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchResultUserViewModel @Inject constructor(private val service: MusicApiService):ViewModel() {
    /**获取搜索结果中的用户部分*/
    fun getSearchUserResult(keywords:String) = creator { offset, limit ->
//        val response = service.getSearchUserResult(keywords = keywords,offset = offset*limit,limit = limit)
        val response = service.getSearchUserResult(keywords = keywords,offset = offset,limit = limit)
        response.result.userprofiles
    }.flow.cachedIn(viewModelScope)
}