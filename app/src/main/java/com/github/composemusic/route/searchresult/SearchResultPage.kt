package com.github.composemusic.route.searchresult

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.github.composemusic.R
import com.github.composemusic.route.drawer.recent.MusicScrollableTabRow
import com.github.composemusic.route.nav.recommend.playlist.transformNum
import com.github.composemusic.route.search.SearchEvent
import com.github.composemusic.route.search.SearchStatus
import com.github.composemusic.route.searchresult.album.SearchResultAlbumList
import com.github.composemusic.route.searchresult.artist.SearchResultArtistList
import com.github.composemusic.route.searchresult.dj.SearchResultDjList
import com.github.composemusic.route.searchresult.mv.SearchResultMvList
import com.github.composemusic.route.searchresult.playlist.SearchResultPlaylistList
import com.github.composemusic.route.searchresult.song.SearchResultSongList
import com.github.composemusic.route.searchresult.user.SearchResultUserList
import com.github.composemusic.route.searchresult.video.SearchResultVideoList
import com.github.composemusic.tool.SearchBar
import com.github.composemusic.ui.theme.ComposeMusicTheme
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "RememberReturnType")
@Composable
fun SearchResultPage(
    viewModel: SearchResultViewModel = hiltViewModel(),
    onSongItem: (Long) -> Unit,
    onAlbumItem: (Long) -> Unit,
    onPlaylistItem: (Long) -> Unit,
    onArtistItem: (Long) -> Unit,
    onDjItem: (Long) -> Unit,
    onBack: () -> Unit,
    onItemMv: (Long) -> Unit,
    onUser: (Long) -> Unit
) {
    val tabs = remember { SearchResult.values().map { it.tab } }
    val scaffoldState = rememberScaffoldState()
    val pagerState = rememberPagerState { tabs.size }
    val scope = rememberCoroutineScope()
    val value = viewModel.uiStatus.value

    LaunchedEffect(scaffoldState.snackbarHostState) {
        viewModel.eventFlow.collectLatest {
            when (it) {
                is SearchStatus.SearchEmpty -> {
                    scope.launch { scaffoldState.snackbarHostState.showSnackbar(it.msg) }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .fillMaxSize()
            .background(ComposeMusicTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Surface(
            color = ComposeMusicTheme.colors.background,
            modifier = Modifier
                .fillMaxSize()
                .background(ComposeMusicTheme.colors.background)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp)
            ) {
                // 搜索栏
                item {
                    SearchBar(
                        value = value.buffer,
                        hint = value.hint,
                        onValueChange = { viewModel.onEvent(SearchEvent.ChangeKey(it)) },
                        onSearch = { viewModel.onEvent(SearchEvent.Search("")) },
                        onBack = onBack
                    )
                }
                // TabRow
                item { MusicScrollableTabRow(pagerState = pagerState, tabs = tabs) }
                // Pager
                item {
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { index ->
                        when (tabs[index]) {
                            SearchResult.Song.tab -> {
                                SearchResultSongList(value.keyword, onSongItem = {
                                    viewModel.onEvent(SearchEvent.InsertMusicItem(it))
                                    onSongItem(it.id)
                                })
                            }
                            SearchResult.Album.tab -> {
                                SearchResultAlbumList(value.keyword, onAlbum = onAlbumItem)
                            }
                            SearchResult.Artist.tab -> {
                                SearchResultArtistList(value.keyword, onArtist = onArtistItem)
                            }
                            SearchResult.Playlist.tab -> {
                                SearchResultPlaylistList(value.keyword, onPlaylist = onPlaylistItem)
                            }
                            SearchResult.Video.tab -> {
                                SearchResultVideoList(value.keyword, onItemMv = onItemMv)
                            }
                            SearchResult.Mv.tab -> {
                                SearchResultMvList(value.keyword, onItemMv = onItemMv)
                            }
                            SearchResult.Dj.tab -> {
                                SearchResultDjList(value.keyword, onDj = onDjItem)
                            }
                            SearchResult.User.tab -> {
                                SearchResultUserList(value.keyword, onUser = onUser)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 搜索结果-item
@Composable
fun SearchResultItem(
    cover: String,
    nickname: String,
    author: String,
    onClick: () -> Unit
) {
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .clickable { onClick() }
    ) {
        val (pic, title, num, more) = createRefs()
        AsyncImage(
            model = cover,
            contentDescription = nickname,
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
        //歌曲名字
        androidx.compose.material3.Text(
            text = nickname,
            style = MaterialTheme.typography.body2,
            color = ComposeMusicTheme.colors.textTitle,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(pic.top, 2.dp)
                start.linkTo(pic.end, 10.dp)
                end.linkTo(more.start)
                width = Dimension.fillToConstraints
            }
        )
        //歌手名称
        androidx.compose.material3.Text(
            text = author,
            style = MaterialTheme.typography.caption,
            color = ComposeMusicTheme.colors.textContent,
            modifier = Modifier.constrainAs(num) {
                bottom.linkTo(pic.bottom, 2.dp)
                start.linkTo(pic.end, 10.dp)
            }
        )
        Icon(
            painter = painterResource(id = R.drawable.icon_more),
            contentDescription = stringResource(id = R.string.more),
            tint = ComposeMusicTheme.colors.textTitle,
            modifier = Modifier
                .size(24.dp)
                .constrainAs(more) {
                    top.linkTo(title.top)
                    bottom.linkTo(num.bottom)
                    end.linkTo(parent.end, 5.dp)
                }
        )
    }
}

// 搜索结果-用户item
@Composable
fun SearchResultUserItem(
    cover: String,
    nickname: String,
    isFollow: Boolean,
    onClick: () -> Unit
) {
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .clickable { onClick() }
    ) {
        val (pic, title, follow) = createRefs()
        AsyncImage(
            model = cover,
            contentDescription = nickname,
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
        androidx.compose.material3.Text(
            text = nickname,
            style = MaterialTheme.typography.body2,
            color = ComposeMusicTheme.colors.textTitle,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(pic.top)
                bottom.linkTo(pic.bottom)
                start.linkTo(pic.end, 10.dp)
                end.linkTo(follow.start)
                width = Dimension.fillToConstraints
            }
        )
        Icon(
            painter = painterResource(id = R.drawable.icon_follow),
            contentDescription = stringResource(id = R.string.follow),
            tint = if (isFollow) ComposeMusicTheme.colors.selectIcon else ComposeMusicTheme.colors.unselectIcon,
            modifier = Modifier
                .size(24.dp)
                .constrainAs(follow) {
                    top.linkTo(pic.top)
                    bottom.linkTo(pic.bottom)
                    end.linkTo(parent.end, 5.dp)
                }
        )
    }
}

// 搜索结果-视频item
@Composable
fun SearchResultVideoItem(
    cover: String,
    title: String,
    author: String,
    playTime: Long,
    durationTime: Int,
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
            model = cover,
            contentDescription = title,
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
        // 视频名称
        androidx.compose.material3.Text(
            text = title,
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
        // 视频作者
        androidx.compose.material3.Text(
            text = author,
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
        // 视频播放次数
        androidx.compose.material3.Text(
            text = "${transformNum(playTime)}次播放",
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
        // 视频播放时间
        androidx.compose.material3.Text(
            text = transformTime(durationTime),
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

// 将整型播放时间转为日期形式
fun transformTime(durationTime: Int): String {
    val date = Date(durationTime.toLong())
    val format = SimpleDateFormat("mm:ss", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("UTC+8");
    return format.format(date)
}

private enum class SearchResult(val tab: String) {
    Song("歌曲"),
    Album("专辑"),
    Playlist("歌单"),
    Artist("艺术家"),
    Video("视频"),
    Dj("DJ"),
    Mv("MV"),
    User("用户")
}