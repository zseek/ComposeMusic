package com.github.composemusic.route.nav.mine

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.github.composemusic.R
import com.github.composemusic.bean.playlist.Playlist
import com.github.composemusic.parm.Constants
import com.github.composemusic.route.playlist.NetworkStatus
import com.github.composemusic.tool.Loading
import com.github.composemusic.tool.LoadingFailed
import com.github.composemusic.ui.theme.ComposeMusicTheme
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MinePage(viewModel: MineViewModel = hiltViewModel(), onPlaylist: (Long) -> Unit) {
    val value = viewModel.uiStatus.value
    val scrollState = rememberScrollState()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(scaffoldState.snackbarHostState) {
        viewModel.eventFlow.collectLatest { // 收集ViewModel中的事件流
            scaffoldState.snackbarHostState.showSnackbar(it) // 显示Snackbar
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(hostState = it) { data ->
                Snackbar(snackbarData = data, modifier = Modifier.navigationBarsPadding())
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(color = ComposeMusicTheme.colors.background)
                .padding(start = 20.dp, end = 20.dp, bottom = 80.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
            ) {
                // 根据加载状态显示加载中及加载错误情况, 加载成功则会跳过
                when (value.playlistState) {
                    is NetworkStatus.Waiting -> {
                        item { Loading() }
                    }
                    is NetworkStatus.Failed -> {
                        item { LoadingFailed(content = value.playlistState.error) {} }
                    }
                    is NetworkStatus.Successful -> {
                        if (value.mapPlaylist.isEmpty()) {
                            item { LoadingFailed(content = stringResource(id = R.string.not_playlist)) {} }
                        }
                    }
                }

                value.mapPlaylist.forEach {
                    item { PlaylistStickyHeader(it) }

                    // 处理显示不同类型的歌单
                    when (it.key) {
                        // 喜欢的音乐
                        Constants.Preference -> item {
                            value.preferBean?.let {
                                PlaylistItem(value.preferBean!!, onPlaylist = onPlaylist)
                                Spacer(modifier = Modifier.height(15.dp))
                            }
                        }
                        // 创建的歌单
                        Constants.Create -> items(value.creates.size) { index ->
                            if (value.creates.isNotEmpty()) {
                                PlaylistItem(value.creates[index], onPlaylist = onPlaylist)
                                if (index < value.creates.size - 1) Spacer(modifier = Modifier.height(10.dp))
                                else Spacer(modifier = Modifier.height(15.dp))
                            }
                        }
                        // 收藏的歌单
                        Constants.Favorite -> items(value.favorites.size) { index ->
                            if (value.favorites.isNotEmpty()) {
                                PlaylistItem(value.favorites[index], onPlaylist = onPlaylist)
                                if (index < value.favorites.size - 1) Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

/** 歌单标题 */
@Composable
private fun PlaylistStickyHeader(pair: Map.Entry<String, Boolean>) {
    if (pair.value) {
        Text(
            text = pair.key,
            style = MaterialTheme.typography.h6,
            color = ComposeMusicTheme.colors.textTitle
        )
        Spacer(modifier = Modifier.height(5.dp))
    }
}

/** 歌单条目
 * @param bean 歌单数据
 * @param onPlaylist 点击事件
 */
@Composable
fun PlaylistItem(bean: Playlist, onPlaylist: (Long) -> Unit) {
    ConstraintLayout(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(ComposeMusicTheme.colors.background)
            .clickable { onPlaylist(bean.id) } // 点击事件, 传入歌单id
    ) {
        val (pic, title, num, more) = createRefs()
        AsyncImage(
            model = bean.coverImgUrl,
            contentDescription = bean.name,
            placeholder = painterResource(id = R.drawable.composemusic_logo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(10.dp))
                .constrainAs(pic) { // 使用约束设置位置
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
        )
        Text(
            text = bean.name,
            style = MaterialTheme.typography.body2,
            color = ComposeMusicTheme.colors.textTitle,
            maxLines = 1,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(pic.top, 2.dp)
                start.linkTo(pic.end, 10.dp)
                end.linkTo(more.start)
                width = Dimension.fillToConstraints
            }
        )
        Text(
            text = "${bean.trackCount} Songs",
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

