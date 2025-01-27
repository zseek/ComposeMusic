package com.github.composemusic.route.searchresult.song

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.github.composemusic.bean.song.Track
import com.github.composemusic.route.searchresult.SearchResultItem
import com.github.composemusic.tool.Loading
import com.github.composemusic.tool.LoadingFailed
import com.github.composemusic.ui.theme.ComposeMusicTheme

@Composable
fun SearchResultSongList(
    keyword: String,
    viewModel: SearchResultSongViewModel = hiltViewModel(),
    onSongItem: (Track) -> Unit
) {
    val songs = viewModel.getSearchSongResult(keyword).collectAsLazyPagingItems()
    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
                .background(ComposeMusicTheme.colors.background),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (songs.loadState.refresh) {
                is LoadState.Loading -> { item { Loading() } }
                is LoadState.Error -> { item { LoadingFailed { songs.retry() } } }
                else -> {}
            }
            items(items = songs) { song ->
                if (song != null) {
                    SearchResultItem(
                        cover = song.al.picUrl,
                        nickname = song.name,
                        author = song.ar[0].name,
                        onClick = {
                            onSongItem(song)
                        }
                    )
                }
            }
            when (songs.loadState.append) {
                is LoadState.Loading -> { item { Loading() } }
                else -> {}
            }
        }
    }
}