package com.github.composemusic.route.search

import com.github.composemusic.bean.search.HotSearch
import com.github.composemusic.bean.search.SearchRecordBean
import com.github.composemusic.bean.search.SearchSuggestionBean

data class SearchUIStatus(
    val keywords: String = "",
    val default: String = "请输入一些关键字...",
    val hots: List<HotSearch> = emptyList(),
    val suggestions: SearchSuggestionBean? = null,
    val isEmptySuggestions: Boolean = true,
    val isShowClear: Boolean = false,
    val isShowDialog: Boolean = false,
    val histories: List<SearchRecordBean> = emptyList()
)
