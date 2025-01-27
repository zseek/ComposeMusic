package com.github.composemusic.route.drawer.favorite

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.github.composemusic.R
import com.github.composemusic.bean.albums.FavoriteAlbumBean
import com.github.composemusic.bean.artist.Artist
import com.github.composemusic.bean.video.VideoBean
import com.github.composemusic.route.drawer.recent.TopTitleBar
import com.github.composemusic.route.nav.recommend.MusicTabRow
import com.github.composemusic.route.searchresult.SearchResultItem
import com.github.composemusic.route.searchresult.SearchResultVideoItem
import com.github.composemusic.tool.Loading
import com.github.composemusic.tool.LoadingFailed
import com.github.composemusic.ui.theme.ComposeMusicTheme
import com.google.accompanist.insets.statusBarsPadding

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoritePage(
    viewModel: FavoriteViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onArtist: (Long) -> Unit,
    onAlbum: (Long) -> Unit,
    onVideo: (String) -> Unit
) {
    val tabs = remember { FavoriteTab.values().map { it.tab } }
    val pageState = androidx.compose.foundation.pager.rememberPagerState { tabs.size }
    val artists = viewModel.getFavoriteArtists().collectAsLazyPagingItems()
    val albums = viewModel.getFavoriteAlbums().collectAsLazyPagingItems()
    val mvs = viewModel.getFavoriteMvs().collectAsLazyPagingItems()
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(ComposeMusicTheme.colors.background)
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        TopTitleBar(onBack = onBack, title = "收藏")
        MusicTabRow(pagerState = pageState, tabs = tabs)
        androidx.compose.foundation.pager.HorizontalPager(state = pageState) { index ->
            when (tabs[index]) {
                FavoriteTab.Artist.tab -> { FavoriteArtistList(state = artists, onArtist = onArtist) }
                FavoriteTab.Album.tab -> { FavoriteAlbumList(state = albums, onAlbum = onAlbum) }
                FavoriteTab.Video.tab -> { FavoriteMvList(state = mvs, onVideo = onVideo) }
            }
        }
    }
}

@Composable
private fun FavoriteArtistList(state: LazyPagingItems<Artist>, onArtist: (Long) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(ComposeMusicTheme.colors.background),
    ) {
        when (state.loadState.refresh) {
            is LoadState.Loading -> { item { Loading() } }
            is LoadState.Error -> { item { LoadingFailed { state.retry() } } }
            is LoadState.NotLoading -> {
                if (state.itemSnapshotList.isEmpty()) {
                    item { LoadingFailed(content = "您还没有收藏过任何歌手") {} }
                }
            }
        }
        items(items = state) { item ->
            item?.let { bean ->
                ArtistItem(
                    artist = bean,
                    onClick = { onArtist(bean.id) }
                )
            }
        }
    }
}

@Composable
private fun FavoriteMvList(state: LazyPagingItems<VideoBean>, onVideo: (String) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(ComposeMusicTheme.colors.background),
    ) {
        when (state.loadState.refresh) {
            is LoadState.Loading -> { item { Loading() } }
            is LoadState.Error -> { item { LoadingFailed { state.retry() } } }
            is LoadState.NotLoading -> {
                if (state.itemSnapshotList.isEmpty()) {
                    item { LoadingFailed(content = "您还没有收藏过任何MV") {} }
                }
            }
        }
        items(items = state) { item ->
            item?.let { bean ->
                SearchResultVideoItem(
                    cover = bean.coverUrl,
                    title = bean.title,
                    author = bean.creator[0].userName,
                    playTime = bean.playTime,
                    durationTime = bean.durationms,
                    onClick = { onVideo(bean.vid) }
                )
            }
        }
    }
}

@Composable
private fun FavoriteAlbumList(state: LazyPagingItems<FavoriteAlbumBean>, onAlbum: (Long) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(ComposeMusicTheme.colors.background),
    ) {
        when (state.loadState.refresh) {
            is LoadState.Loading -> { item { Loading() } }
            is LoadState.Error -> { item { LoadingFailed { state.retry() } } }
            is LoadState.NotLoading -> {
                if (state.itemSnapshotList.isEmpty()) {
                    item { LoadingFailed(content = "您还没有收藏过任何专辑") {} }
                }
            }
        }
        items(items = state) { item ->
            item?.let { bean ->
                SearchResultItem(
                    cover = bean.picUrl,
                    nickname = bean.name,
                    author = bean.artists[0].name,
                    onClick = { onAlbum(bean.id) }
                )
            }
        }
    }
}

@Composable
private fun ArtistItem(artist: Artist, onClick: (Long) -> Unit) {
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .clickable { onClick(artist.id) }
    ) {
        val (pic, title, more) = createRefs()
        AsyncImage(
            model = artist.picUrl,
            contentDescription = artist.name,
            placeholder = painterResource(id = R.drawable.composemusic_logo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(10.dp))
                .constrainAs(pic) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
        )
        //名称
        Text(
            text = artist.name,
            style = MaterialTheme.typography.body2,
            color = ComposeMusicTheme.colors.textTitle,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(pic.top)
                bottom.linkTo(pic.bottom)
                start.linkTo(pic.end, 10.dp)
                end.linkTo(more.start)
                width = Dimension.fillToConstraints
            }
        )
        Icon(
            painter = painterResource(id = R.drawable.icon_more),
            contentDescription = "更多",
            tint = ComposeMusicTheme.colors.textTitle,
            modifier = Modifier
                .size(24.dp)
                .constrainAs(more) {
                    top.linkTo(pic.top)
                    bottom.linkTo(pic.bottom)
                    end.linkTo(parent.end, 5.dp)
                }
        )
    }
}

private enum class FavoriteTab(val tab: String) {
    Album("专辑"),
    Video("MV"),
    Artist("艺术家")
}