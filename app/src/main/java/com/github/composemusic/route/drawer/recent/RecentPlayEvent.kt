package com.github.composemusic.route.drawer.recent

sealed class RecentPlayEvent {
    data class PlaySong(val index: Int) : RecentPlayEvent()
}