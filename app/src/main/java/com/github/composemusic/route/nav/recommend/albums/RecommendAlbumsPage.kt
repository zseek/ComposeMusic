package com.github.composemusic.route.nav.recommend.albums

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
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
import com.github.composemusic.bean.albums.AlbumsBean
import com.github.composemusic.bean.albums.DigitAlbumsBean
import com.github.composemusic.tool.Loading
import com.github.composemusic.ui.theme.ComposeMusicTheme
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecommendAlbumsPage(
    viewModel: AlbumViewModel = hiltViewModel(),
    onAlbum: (Long) -> Unit
) {
    val value = viewModel.uiStatus.value
    val headers = remember { AlbumTab.values() } // 记录页面不同部分的标题枚举类
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
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
                .background(color = ComposeMusicTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            if (value.albums.isEmpty() || value.digitAlbums.isEmpty() || value.albumsRank.isEmpty()) {
                item { Loading() }
            }
            headers.forEach { header ->
                item { // 不同部分的标题
                    Text(
                        text = header.msg,
                        style = MaterialTheme.typography.body1,
                        color = ComposeMusicTheme.colors.textTitle
                    )
                }
                when (header) { // 根据标题显示不同部分的内容
                    AlbumTab.New -> {
                        item {
                            RecommendAlbums(albums = value.albums, onAlbum)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }

                    AlbumTab.Digit -> {
                        item {
                            RecommendDigitAlbums(value.digitAlbums, onAlbum)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }

                    AlbumTab.Rank -> {
                        items(value.albumsRank.size) {
                            AlbumRankItem(value.albumsRank[it], onAlbum)
                            if (it < value.albumsRank.size - 1)
                                Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

// 由于AlbumsBean和DigitAlbumsBean的数据结构不同，所以需要分别处理
@Composable
private fun RecommendAlbums(albums: List<AlbumsBean>, onAlbum: (Long) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(ComposeMusicTheme.colors.background)
    ) {
        items(albums.size) {
            RecommendAlbumItem(albums[it], onAlbum = onAlbum)
            if (it < albums.size - 1)
                Spacer(modifier = Modifier.width(15.dp))
        }
    }
}
@Composable
private fun RecommendDigitAlbums(albums: List<DigitAlbumsBean>, onAlbum: (Long) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(ComposeMusicTheme.colors.background)
    ) {
        items(albums.size) {
            RecommendDigitAlbumItem(albums[it], onAlbum = onAlbum)
            if (it < albums.size - 1)
                Spacer(modifier = Modifier.width(15.dp))
        }
    }
}

@Composable
private fun RecommendAlbumItem(
    bean: AlbumsBean,
    maxHeight: Int = LocalConfiguration.current.screenHeightDp,
    maxWidth: Int = LocalConfiguration.current.screenWidthDp,
    onAlbum: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .height((maxHeight / 5).dp)
            .width((maxWidth / 4).dp)
            .background(ComposeMusicTheme.colors.background)
            .clickable { onAlbum(bean.id) },
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = bean.picUrl,
            contentDescription = bean.name,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.composemusic_logo),
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "${bean.artist.name} | ${bean.name}",
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@Composable
private fun RecommendDigitAlbumItem(
    bean: DigitAlbumsBean,
    maxHeight: Int = LocalConfiguration.current.screenHeightDp,
    maxWidth: Int = LocalConfiguration.current.screenWidthDp,
    onAlbum: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .height((maxHeight / 5).dp)
            .width((maxWidth / 4).dp)
            .background(ComposeMusicTheme.colors.background)
            .clickable { onAlbum(bean.albumId.toLong()) },
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = bean.coverUrl,
            contentDescription = bean.albumName,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.composemusic_logo),
            modifier = Modifier.weight(1f).clip(RoundedCornerShape(20.dp))
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "${bean.artistName} | ${bean.albumName}",
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
    }
}



@Composable
private fun AlbumRankItem(bean: DigitAlbumsBean, onAlbum: (Long) -> Unit) {
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .background(ComposeMusicTheme.colors.background)
        .clickable { onAlbum(bean.albumId.toLong()) }
    ) {
        val (pic, title, num, more) = createRefs()
        AsyncImage(
            model = bean.coverUrl,
            contentDescription = bean.albumName,
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
            text = bean.albumName,
            style = MaterialTheme.typography.body2,
            color = ComposeMusicTheme.colors.textTitle,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(pic.top, 2.dp)
                start.linkTo(pic.end, 10.dp)
                end.linkTo(more.start)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = bean.artistName,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            color = ComposeMusicTheme.colors.textContent,
            modifier = Modifier.constrainAs(num) {
                bottom.linkTo(pic.bottom, 2.dp)
                start.linkTo(pic.end, 10.dp)
                end.linkTo(more.start)
                width = Dimension.fillToConstraints
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

private enum class AlbumTab(val msg: String) {
    New("最新专辑"),
    Digit("数字专辑"),
    Rank("专辑榜")
}


