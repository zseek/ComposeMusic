package com.github.composemusic.route.searchresult.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.composemusic.datapaging.creator
import com.github.composemusic.network.MusicApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchResultPlaylistViewModel @Inject constructor(private val service: MusicApiService):ViewModel() {
    /**
     * 获取搜索结果中的歌单部分
     * 使用 creator 创建Pager实例, Pager提供了一个flow属性, 使用.flow可以将分页数据作为 Flow（数据流）返回
     * cachedIn(viewModelScope) 也是Paging 库中的操作符, 用于在指定作用域内缓存数据
     * 返回类型为Flow<PagingData<Playlist>>, 使用时使用 collectAsLazyPagingItems() 函数将其转换为 LazyPagingItems<Track>类型,用于Lazy布局中传入items()中
     * 例如: val songs = viewModel.getSearchSongResult(keyword).collectAsLazyPagingItems()
     */
    fun getSearchPlaylistResult(keywords:String) = creator { offset, limit ->
//        val response = service.getSearchPlaylistResult(keywords = keywords,offset = offset*limit,limit = limit)
        val response = service.getSearchPlaylistResult(keywords = keywords,offset = offset,limit = limit)
        response.result.playlists
    }.flow.cachedIn(viewModelScope)
}