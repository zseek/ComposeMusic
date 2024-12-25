package com.github.composemusic.route.nav.recommend.artist

sealed class ArtistEvent {
    object Retry : ArtistEvent()
    object Finish : ArtistEvent()
}
