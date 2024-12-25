package com.github.composemusic.route.search

sealed class SearchStatus(val msg: String) {
    data class SearchSuccess(val key: String) : SearchStatus(msg = key)
    data class Message(val message: String) : SearchStatus(msg = message)
    object SearchFailed : SearchStatus("Not Found!")
    object SearchEmpty : SearchStatus("搜索不能为空!")
    object Clear : SearchStatus("清除成功!")
    object Withdraw : SearchStatus("撤回成功!")
}
