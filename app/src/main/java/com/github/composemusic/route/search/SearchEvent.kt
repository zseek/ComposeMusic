package com.github.composemusic.route.search

import com.github.composemusic.bean.song.Track

sealed class SearchEvent {
    data class ChangeKey(val key: String) : SearchEvent()
    data class Search(val key: String) : SearchEvent()
    data class InsertMusicItem(val bean: Track) : SearchEvent()
    object Clear : SearchEvent()
    object ConfirmClear : SearchEvent()
    object CancelClear : SearchEvent()
    object Withdraw : SearchEvent()
}
