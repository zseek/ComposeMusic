package com.github.composemusic.route.drawer.recent

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.github.composemusic.R
import com.github.composemusic.bean.albums.AlbumsBean
import com.github.composemusic.bean.dj.DjRadioBean
import com.github.composemusic.bean.playlist.Playlist
import com.github.composemusic.bean.recent.RecentPlayBean
import com.github.composemusic.bean.recent.RecentVideoBean
import com.github.composemusic.bean.song.Track
import com.github.composemusic.route.nav.mine.PlaylistItem
import com.github.composemusic.route.nav.recommend.songs.EveryDaySongsItem
import com.github.composemusic.route.playlist.NetworkStatus
import com.github.composemusic.route.searchresult.SearchResultItem
import com.github.composemusic.route.searchresult.transformTime
import com.github.composemusic.tool.Loading
import com.github.composemusic.tool.LoadingFailed
import com.github.composemusic.tool.customIndicatorOffset
import com.github.composemusic.ui.theme.ComposeMusicTheme
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RecentPlayPage(
    viewModel: RecentPlayViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSongItem: (Long) -> Unit,
    onAlbumItem: (Long) -> Unit,
    onPlaylistItem: (Long) -> Unit,
    onDjItem: (Long) -> Unit,
    onMvItem: (Long) -> Unit,
    onMlogItem: (String) -> Unit
) {
    val value = viewModel.uiState.value
    val tabs = remember { RecentPlayTab.values().map { it.tab } }
    val scaffoldState = rememberScaffoldState()
    val pagerState = rememberPagerState { tabs.size }

    LaunchedEffect(key1 = scaffoldState.snackbarHostState) {
        viewModel.eventFlow.collectLatest {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(hostState = it) { data ->
                Snackbar(
                    snackbarData = data,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(ComposeMusicTheme.colors.background)
                .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            TopTitleBar(onBack = onBack)
            MusicScrollableTabRow(pagerState = pagerState, tabs = tabs)
            HorizontalPager(
                state = pagerState
            ) { index ->
                when (tabs[index]) {
                    RecentPlayTab.Song.tab -> {
                        SongList(
                            state = value.songState,
                            songs = value.songs,
                            onSongItem = { id, i ->
                                viewModel.onEvent(RecentPlayEvent.PlaySong(i))
                                onSongItem(id)
                            })
                    }
                    RecentPlayTab.Album.tab -> {
                        AlbumList(
                            state = value.albumState,
                            albums = value.albums,
                            onAlbumItem = onAlbumItem
                        )
                    }
                    RecentPlayTab.Playlist.tab -> {
                        PlaybackList(
                            state = value.playlistState,
                            playlists = value.playlists,
                            onPlaybackItem = onPlaylistItem
                        )
                    }
                    RecentPlayTab.Video.tab -> {
                        VideoList(
                            state = value.videoState,
                            videos = value.videos,
                            onMvItem = onMvItem,
                            onMlogItem = onMlogItem
                        )
                    }
                    RecentPlayTab.RadioStation.tab -> {
                        DjList(
                            state = value.djState,
                            djs = value.djs,
                            onDjItem = onDjItem
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopTitleBar(title: String = "最近播放", onBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "返回",
            tint = ComposeMusicTheme.colors.textTitle,
            modifier = Modifier
                .size(32.dp)
                .clickable { onBack() }
        )
        Text(
            text = title,
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
            color = ComposeMusicTheme.colors.textTitle,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 32.dp)
        )
    }
}

@Composable
fun IconTopTitleBar(
    title: String = "最近播放",
    @DrawableRes icon: Int,
    onBack: () -> Unit,
    onIconClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "Back",
            tint = ComposeMusicTheme.colors.textTitle,
            modifier = Modifier
                .size(32.dp)
                .clickable { onBack() }
        )
        Text(
            text = title,
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
            color = ComposeMusicTheme.colors.textTitle,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(id = icon),
            contentDescription = "Icon",
            tint = ComposeMusicTheme.colors.textTitle,
            modifier = Modifier
                .size(32.dp)
                .clickable { onIconClick() }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicScrollableTabRow(
    pagerState: PagerState,
    tabs: List<String>,
    scope: CoroutineScope = rememberCoroutineScope()
) {
    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        edgePadding = 0.dp,
        indicator = { pos ->
            TabRowDefaults.Indicator(
                color = ComposeMusicTheme.colors.selectIcon,
                modifier = Modifier.customIndicatorOffset(
                    pagerState = pagerState,
                    tabPositions = pos,
                    width = 32.dp
                )
            )
        },
        divider = { TabRowDefaults.Divider(color = Color.Transparent) },
        backgroundColor = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = index == pagerState.currentPage,
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(index) }
                },
                selectedContentColor = ComposeMusicTheme.colors.selectIcon,
                unselectedContentColor = ComposeMusicTheme.colors.unselectIcon,
                text = {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.subtitle2,
                        color = if (index == pagerState.currentPage) ComposeMusicTheme.colors.textTitle else ComposeMusicTheme.colors.textContent,
                    )
                }
            )
        }
    }
}


@Composable
private fun SongList(
    state: NetworkStatus,
    songs: List<RecentPlayBean<Track>>,
    onSongItem: (Long, Int) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
    ) {
        when (state) {
            is NetworkStatus.Waiting -> {
                item { Loading() }
            }

            is NetworkStatus.Failed -> {
                item { LoadingFailed(content = state.error) {} }
            }

            is NetworkStatus.Successful -> {
                if (songs.isEmpty()) {
                    item { LoadingFailed(content = "Not play song in recent!") {} }
                }
            }
        }
        itemsIndexed(songs) { index, item ->
            EveryDaySongsItem(
                bean = item.data,
                onSongItem = { onSongItem(it.id, index) }
            )
        }
    }
}

@Composable
private fun PlaybackList(
    state: NetworkStatus,
    playlists: List<RecentPlayBean<Playlist>>,
    onPlaybackItem: (Long) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
    ) {
        when (state) {
            is NetworkStatus.Waiting -> {
                item { Loading() }
            }

            is NetworkStatus.Failed -> {
                item { LoadingFailed(content = state.error) {} }
            }

            is NetworkStatus.Successful -> {
                if (playlists.isEmpty()) {
                    item { LoadingFailed(content = "Not play playback in recent!") {} }
                }
            }

        }

        itemsIndexed(playlists) { index, item ->
            PlaylistItem(
                bean = item.data,
                onPlaylist = onPlaybackItem
            )
        }
    }
}

@Composable
private fun AlbumList(
    state: NetworkStatus,
    albums: List<RecentPlayBean<AlbumsBean>>,
    onAlbumItem: (Long) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
    ) {
        when (state) {
            is NetworkStatus.Waiting -> {
                item { Loading() }
            }

            is NetworkStatus.Failed -> {
                item { LoadingFailed(content = state.error) {} }
            }

            is NetworkStatus.Successful -> {
                if (albums.isEmpty()) {
                    item { LoadingFailed(content = "Not play album in recent!") {} }
                }
            }

        }

        itemsIndexed(albums) { index, item ->
            SearchResultItem(
                cover = item.data.blurPicUrl,
                nickname = item.data.name,
                author = item.data.artist.name,
                onClick = { onAlbumItem(item.data.id) }
            )
        }
    }
}

@Composable
private fun VideoList(
    state: NetworkStatus,
    videos: List<RecentVideoBean>,
    onMvItem: (Long) -> Unit,
    onMlogItem: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
    ) {
        when (state) {
            is NetworkStatus.Waiting -> {
                item { Loading() }
            }

            is NetworkStatus.Failed -> {
                item { LoadingFailed(content = state.error) {} }
            }

            is NetworkStatus.Successful -> {
                if (videos.isEmpty()) {
                    item { LoadingFailed(content = "Not play video in recent!") {} }
                }
            }

        }

        itemsIndexed(videos) { index, item ->
            RecentVideoItem(
                video = item,
                onClick = {
                    if (item.tag == "MV") {
                        onMvItem(item.id)
                    } else {
                        onMlogItem(item.idStr)
                    }
                }
            )
        }
    }
}

@Composable
private fun DjList(
    state: NetworkStatus,
    djs: List<RecentPlayBean<DjRadioBean>>,
    onDjItem: (Long) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
    ) {
        when (state) {
            is NetworkStatus.Waiting -> {
                item { Loading() }
            }

            is NetworkStatus.Failed -> {
                item { LoadingFailed(content = state.error) {} }
            }

            is NetworkStatus.Successful -> {
                if (djs.isEmpty()) {
                    item { LoadingFailed(content = "Not play radio in recent!") {} }
                }
            }

        }

        itemsIndexed(djs) { index, item ->
            SearchResultItem(
                cover = item.data.picUrl,
                nickname = item.data.name,
                author = item.data.dj.nickname,
                onClick = { onDjItem(item.data.id) }
            )
        }
    }
}


@Composable
private fun RecentVideoItem(
    video: RecentVideoBean,
    maxHeight: Int = LocalConfiguration.current.screenHeightDp,
    maxWidth: Int = LocalConfiguration.current.screenWidthDp,
    onClick: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height((maxHeight / 6).dp)
            .clickable {
                onClick()
            }
    ) {
        val (coverRes, titleRes, authorRes, playTimeRes, durationTimeRes) = createRefs()
        AsyncImage(
            model = video.cover,
            contentDescription = video.name,
            placeholder = painterResource(id = R.drawable.composemusic_logo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxHeight()
                .width((maxWidth / 3).dp)
                .clip(RoundedCornerShape(10.dp))
                .constrainAs(coverRes) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
        )
        /**
         * 视频名称*/
        androidx.compose.material3.Text(
            text = video.name,
            style = MaterialTheme.typography.body1,
            color = ComposeMusicTheme.colors.textTitle,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(titleRes) {
                top.linkTo(coverRes.top)
                start.linkTo(coverRes.end, 10.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
        /**
         * 视频作者*/
        androidx.compose.material3.Text(
            text = video.artist,
            style = MaterialTheme.typography.subtitle2.copy(fontFamily = FontFamily.Default),
            color = ComposeMusicTheme.colors.highlightColor,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(authorRes) {
                top.linkTo(titleRes.bottom, 10.dp)
                start.linkTo(coverRes.end, 10.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        /**
         * 视频类型*/
        androidx.compose.material3.Text(
            text = video.tag,
            style = MaterialTheme.typography.caption,
            color = ComposeMusicTheme.colors.textContent,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(playTimeRes) {
                top.linkTo(authorRes.bottom, 5.dp)
                start.linkTo(coverRes.end, 10.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        /**
         * 视频播放时间*/
        androidx.compose.material3.Text(
            text = transformTime(video.duration),
            style = MaterialTheme.typography.caption,
            color = ComposeMusicTheme.colors.background,
            textAlign = TextAlign.Start,
            modifier = Modifier.constrainAs(durationTimeRes) {
                bottom.linkTo(coverRes.bottom, 10.dp)
                end.linkTo(coverRes.end, 10.dp)
            }
        )
    }
}

private enum class RecentPlayTab(val tab: String) {
    Song("歌曲"),
    Playlist("歌单"),
    Album("专辑"),
    Video("视频"),
    RadioStation("电台"),
}