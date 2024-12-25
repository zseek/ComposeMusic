package com.github.composemusic.route.artist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.github.composemusic.R
import com.github.composemusic.bean.artist.Artist
import com.github.composemusic.bean.artist.ArtistInfoBean
import com.github.composemusic.route.nav.recommend.MusicTabRow
import com.github.composemusic.route.playlist.AlbumSongsItem
import com.github.composemusic.route.searchresult.SearchResultItem
import com.github.composemusic.route.searchresult.SearchResultVideoItem
import com.github.composemusic.tool.Loading
import com.github.composemusic.ui.theme.ComposeMusicTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistDetailPage(
    viewModel: ArtistDetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onItemMv: (Long) -> Unit,
    onArtist: (Long) -> Unit,
    onSongItem: (Long) -> Unit,
    onAlbumItem: (Long) -> Unit
) {
    val value = viewModel.uiStatus.value
    val tabs = remember { ArtistTab.values().map { it.tab } }
    val pageState = androidx.compose.foundation.pager.rememberPagerState { tabs.size }
    val scrollState = rememberScrollState()
    val coverHeight = (LocalConfiguration.current.screenHeightDp * 0.6).dp

    // 嵌套滚动连接器
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return if (available.y > 0) Offset.Zero else Offset(
                    x = 0f,
                    y = -scrollState.dispatchRawDelta(-available.y)
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState)
            .background(ComposeMusicTheme.colors.grayBackground)
            .navigationBarsPadding()
            .padding(bottom = 10.dp)
    ) {
        ArtistInfo(
            isFollow = value.isFollow,
            artist = value.artist,
            height = coverHeight,
            onBack = onBack
        )
        MusicTabRow(pagerState = pageState, tabs = tabs)
        HorizontalPager(
            state = pageState,
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
        ) { index ->
            when (tabs[index]) {
                ArtistTab.Profile.tab -> {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
                    ) {
                        if (value.artist == null || value.similar.isEmpty()) {
                            item { Loading() }
                        }
                        item { BriefDescription(value.artist?.briefDesc ?: "") }
                        item {
                            Text(
                                text = stringResource(id = R.string.similar),
                                color = ComposeMusicTheme.colors.textTitle,
                                style = MaterialTheme.typography.h6
                            )
                        }
                        item { SimilarArtists(value.similar, onArtist) }
                    }
                }
                ArtistTab.Songs.tab -> {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
                    ) {
                        if (value.songs.isEmpty()) {
                            item { Loading() }
                        }
                        itemsIndexed(value.songs) { index, song ->
                            AlbumSongsItem(
                                bean = song,
                                onSongItem = {
                                    viewModel.playSong(song)
                                    onSongItem(it)
                                },
                                order = index + 1
                            )
                        }
                    }
                }
                ArtistTab.Albums.tab -> {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
                    ) {
                        if (value.albums.isEmpty()) {
                            item { Loading() }
                        }
                        items(value.albums) { album ->
                            SearchResultItem(
                                cover = album.picUrl,
                                nickname = album.name,
                                author = album.artist.name,
                                onClick = { onAlbumItem(album.id) }
                            )
                        }
                    }
                }
                ArtistTab.Mvs.tab -> {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
                    ) {
                        if (value.mvs.isEmpty()) {
                            item { Loading() }
                        }
                        items(value.mvs) { mv ->
                            SearchResultVideoItem(
                                cover = mv.imgurl,
                                title = mv.name,
                                author = mv.artistName,
                                playTime = mv.playCount,
                                durationTime = mv.duration,
                                onClick = { onItemMv(mv.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtistInfo(
    isFollow: Boolean,
    artist: ArtistInfoBean?,
    height: Dp,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
    ) {
        if (artist != null) {
            // 背景图片
            AsyncImage(
                model = artist.cover,
                contentDescription = artist.name,
                placeholder = painterResource(id = R.drawable.composemusic_logo),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 叠加内容
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp) // 为内容留出内边距，避免紧贴边缘
            ) {
                val (nameRef, followButtonRef, backIconRef) = createRefs()

                // 歌手名称和别名
                Column(
                    modifier = Modifier
                        .constrainAs(nameRef) {
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                        }
                ) {
                    Text(
                        text = artist.name,
                        color = ComposeMusicTheme.colors.textTitle,
                        style = MaterialTheme.typography.subtitle1.copy(
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (artist.alias.isNotEmpty()) {
                        Text(
                            text = artist.alias.arrayToString(),
                            color = ComposeMusicTheme.colors.textContent,
                            style = MaterialTheme.typography.overline,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // 关注按钮
                TextButton(
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, ComposeMusicTheme.colors.textContent),
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = if (isFollow) ComposeMusicTheme.colors.selectIcon else ComposeMusicTheme.colors.background
                    ),
                    onClick = { /* 处理关注事件 */ },
                    modifier = Modifier
                        .constrainAs(followButtonRef) {
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                ) {
                    Text(
                        text = if (isFollow) "已关注" else "关注",
                        color = ComposeMusicTheme.colors.textTitle,
                        style = MaterialTheme.typography.overline
                    )
                }

                // 返回图标
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = ComposeMusicTheme.colors.background,
                    modifier = Modifier
                        .constrainAs(backIconRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                        .size(24.dp)
                        .clickable { onBack() }
                )
            }
        }
    }
}

// 歌手简介
@Composable
private fun BriefDescription(description: String) {
    val isExpend = remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .padding(start = 10.dp, end = 10.dp)
            .background(ComposeMusicTheme.colors.background)
            .padding(10.dp)
    ) {
        Text(
            text = description,
            color = ComposeMusicTheme.colors.textContent,
            style = MaterialTheme.typography.caption,
            maxLines = if (isExpend.value) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        Icon(
            painter = painterResource(id = if (isExpend.value) R.drawable.icon_collapse else R.drawable.icon_expend),
            contentDescription = "更多简介",
            tint = ComposeMusicTheme.colors.textTitle,
            modifier = Modifier
                .size(24.dp)
                .clickable { isExpend.value = !isExpend.value }
        )
    }
}

// 相似艺人
@Composable
private fun SimilarArtists(artists: List<Artist>, onArtist: (Long) -> Unit) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(artists) { artist ->
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .height((LocalConfiguration.current.screenHeightDp / 5).dp)
                    .width((LocalConfiguration.current.screenWidthDp / 4).dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ComposeMusicTheme.colors.background)
                    .padding(5.dp)
                    .clickable { onArtist(artist.id) }
            ) {
                AsyncImage(
                    model = artist.picUrl,
                    contentDescription = artist.name,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = artist.name,
                    color = ComposeMusicTheme.colors.textTitle,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
                TextButton(
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, ComposeMusicTheme.colors.textContent),
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = if (artist.followed) ComposeMusicTheme.colors.selectIcon else ComposeMusicTheme.colors.background
                    ),
                    contentPadding = PaddingValues(start = 2.dp, end = 2.dp),
                    onClick = { }
                ) {
                    Text(
                        text = if (artist.followed) "已关注" else "关注",
                        color = ComposeMusicTheme.colors.textTitle,
                        style = MaterialTheme.typography.overline
                    )
                }
            }
        }
    }
}

// 扩展函数,将List<String>转换为一个包含分隔符(、)的字符串
private fun List<String>.arrayToString(): String {
    var result = ""
    this.forEachIndexed { index, item ->
        result += item
        if (index < this.size - 1) result += "、"
    }
    return result
}

private enum class ArtistTab(val tab: String) {
    Profile("简介"),
    Songs("歌曲"),
    Albums("专辑"),
    Mvs("MV")
}