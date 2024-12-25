package com.github.composemusic.bean.searchresult

import com.github.composemusic.bean.dj.DjRadioBean

/**
 * 电台、广播*/
data class SearchDjBean(
    val djRadios: List<DjRadioBean>,
    val djRadiosCount: Int,
    val searchQcReminder: Any
)