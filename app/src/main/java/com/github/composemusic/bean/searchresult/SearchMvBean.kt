package com.github.composemusic.bean.searchresult

import com.github.composemusic.bean.mv.MvBean

data class SearchMvBean(
    val code: Int,
    val mvCount: Int,
    val mvs: List<MvBean>
)