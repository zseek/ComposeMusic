package com.github.composemusic.route.nav.recommend.artist

sealed class ArtistStatus(val status: String) {
    object Retry : ArtistStatus("加载失败，请重试！")
    object Finish : ArtistStatus("到底了！")
}
