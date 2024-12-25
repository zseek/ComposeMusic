package com.github.composemusic.route.nav.recommend.playlist

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.github.composemusic.R
import com.github.composemusic.bean.recommend.playlist.Result
import com.github.composemusic.tool.Loading
import com.github.composemusic.ui.theme.ComposeMusicTheme
import kotlinx.coroutines.flow.collectLatest
import java.text.DecimalFormat

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecommendPlaylistPage(viewModel: RePlaylistViewModel = hiltViewModel(), onPlaylist: (id: Long) -> Unit) {
    val value = viewModel.uiStatus.value
    val headers = remember { PlaylistTab.values() }

    val scaffoldState = rememberScaffoldState()

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
        },
        modifier = Modifier
            .fillMaxSize()
            .background(ComposeMusicTheme.colors.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
                .background(color = ComposeMusicTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            if (value.dayPlaylist.isEmpty()) {
                item { Loading() }
            }
            headers.forEach { header ->
                stickyHeader {
                    RecommendPlaylistStickyHeader(header = header.msg)
                    Spacer(modifier = Modifier.height(5.dp))
                }
                when (header) {
                    PlaylistTab.Daily -> {
                        item {
                            RecommendPlaylist(value.newPlaylist) {
                                onPlaylist(it.id)
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }

                    PlaylistTab.New -> {
                        items(value.dayPlaylist.size) {
                            EveryDayPlaylistItem(value.dayPlaylist[it]) { playlist ->
                                onPlaylist(playlist.id)
                            }
                            if (it < value.dayPlaylist.size - 1)
                                Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

// 网易云推荐歌单
@Composable
private fun RecommendPlaylist(bean: List<Result>, onPlaylist: (Result) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(ComposeMusicTheme.colors.background)
    ) {
        items(bean.size) {
            RecommendPlaylistItem(bean[it], onPlaylist = onPlaylist)
            if (it < bean.size - 1)
                Spacer(modifier = Modifier.width(15.dp))
        }
    }
}

@Composable
private fun RecommendPlaylistItem(
    bean: Result,
    maxHeight: Int = LocalConfiguration.current.screenHeightDp,
    maxWidth: Int = LocalConfiguration.current.screenWidthDp,
    onPlaylist: (Result) -> Unit
) {
    Box(
        modifier = Modifier
            .height((maxHeight / 6).dp)
            .width((maxWidth / 3).dp)
            .background(ComposeMusicTheme.colors.background)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onPlaylist(bean) }
    ) {
        AsyncImage(
            model = bean.picUrl,
            contentDescription = bean.name,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.composemusic_logo),
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(5.dp)
        ) {
            Text(
                text = bean.name,
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = ComposeMusicTheme.colors.textContent,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// 个性每日推荐歌单
@Composable
private fun EveryDayPlaylistItem(bean: Recommend, onPlaylist: (Recommend) -> Unit) {
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .background(ComposeMusicTheme.colors.background)
        .clickable { onPlaylist(bean) }
    ) {
        val (pic, title, num, more) = createRefs()
        AsyncImage(
            model = bean.picUrl,
            contentDescription = bean.name,
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

/********************************************************************/
@Composable
fun RecommendPlaylistStickyHeader(
    header: String,
    style: TextStyle = MaterialTheme.typography.body1 // 之前为h6
) {
    Text(
        text = header,
        style = style,
        color = ComposeMusicTheme.colors.textTitle
    )
}

fun transformNum(num: Long): String {
    if (num < 10000) return "$num"
    return "${remainDigit(num / 10000.0)}W"
}
/**保留二位小数*/
fun remainDigit(num: Double): String {
    val df = DecimalFormat("#.00")
    return df.format(num)
}
private enum class PlaylistTab(val msg: String) {
    Daily("网易云每日推荐"),
    New("个性每日推荐歌单"),
}