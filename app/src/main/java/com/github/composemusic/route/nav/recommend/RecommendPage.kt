package com.github.composemusic.route.nav.recommend

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.composemusic.route.nav.recommend.albums.RecommendAlbumsPage
import com.github.composemusic.route.nav.recommend.artist.RecommendArtistPage
import com.github.composemusic.route.nav.recommend.playlist.RecommendPlaylistPage
import com.github.composemusic.route.nav.recommend.songs.RecommendSongsPage
import com.github.composemusic.tool.customIndicatorOffset
import com.github.composemusic.ui.theme.ComposeMusicTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.pager.HorizontalPager

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecommendPage(
    onSongItem:(Long)->Unit,
    onPlaylist: (id:Long) -> Unit,
    onAlbum:(Long)->Unit,
    onArtist:(Long)->Unit
){
    //val pageState = rememberPagerState(initialPage = 0)
    val tabs = remember { RecommendTab.values().map { it.tab } }
    val pageState = androidx.compose.foundation.pager.rememberPagerState{ tabs.size }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = ComposeMusicTheme.colors.background)
            .padding(start = 20.dp, end = 20.dp, bottom = 80.dp)
    ) {
        MusicTabRow(
            pagerState = pageState,
            tabs = tabs
        )
        Spacer(modifier = Modifier.height(10.dp))

        HorizontalPager(state = pageState) {
            when(tabs[it]){
                RecommendTab.RecommendSongs.tab->{ RecommendSongsPage(onSongItem = onSongItem)}
                RecommendTab.RecommendPlaylist.tab->{ RecommendPlaylistPage(onPlaylist = onPlaylist)}
                RecommendTab.RecommendAlbums.tab->{ RecommendAlbumsPage(onAlbum = onAlbum)}
                RecommendTab.RecommendArtist.tab->{ RecommendArtistPage(onArtist = onArtist)}
            }
        }
    }
}

@OptIn( ExperimentalFoundationApi::class)
@Composable
fun MusicTabRow(
    pagerState: androidx.compose.foundation.pager.PagerState,
    tabs:List<String>,
    scope: CoroutineScope = rememberCoroutineScope()
){
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { pos ->
            TabRowDefaults.Indicator(
                color = ComposeMusicTheme.colors.selectIcon,
                modifier = Modifier.customIndicatorOffset(
                    pagerState = pagerState,
                    tabPositions = pos,
                    width = 32.dp
                )) },
        divider = {TabRowDefaults.Divider(color = Color.Transparent)},
        backgroundColor = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                text = {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.subtitle2,
                        color = if (pagerState.currentPage == index) ComposeMusicTheme.colors.textTitle else ComposeMusicTheme.colors.textContent
                    )},
                selected = pagerState.currentPage == index,
                enabled = true,
                selectedContentColor = ComposeMusicTheme.colors.selectIcon,
                unselectedContentColor = ComposeMusicTheme.colors.unselectIcon,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                })
        }
    }
}

private enum class RecommendTab(val tab:String){
    RecommendSongs("歌曲"),
    RecommendPlaylist("歌单"),
    RecommendAlbums("专辑"),
    RecommendArtist("艺术家")
}

