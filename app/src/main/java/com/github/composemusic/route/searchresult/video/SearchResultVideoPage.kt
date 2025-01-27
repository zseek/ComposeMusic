package com.github.composemusic.route.searchresult.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.github.composemusic.route.searchresult.SearchResultVideoItem
import com.github.composemusic.tool.Loading
import com.github.composemusic.tool.LoadingFailed
import com.github.composemusic.ui.theme.ComposeMusicTheme

@Composable
fun SearchResultVideoList(
    keyword: String,
    viewModel: SearchResultVideoViewModel = hiltViewModel(),
    onItemMv: (Long) -> Unit
) {
    val videos = viewModel.getSearchVideoResult(keyword).collectAsLazyPagingItems()
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
            .background(ComposeMusicTheme.colors.background),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        when (videos.loadState.refresh) {
            is LoadState.Loading -> { item { Loading() } }
            is LoadState.Error -> { item { LoadingFailed { videos.retry() } } }
            else -> {}
        }
        items(items = videos) { video ->
            if (video != null) {
                SearchResultVideoItem(
                    cover = video.coverUrl,
                    title = video.title,
                    author = video.creator[0].userName,
                    playTime = video.playTime,
                    durationTime = video.durationms,
                    onClick = { onItemMv(video.vid.toLong()) }
                )
            }
        }
        when (videos.loadState.append) {
            is LoadState.Loading -> { item { Loading() } }
            else -> {}
        }
    }
}