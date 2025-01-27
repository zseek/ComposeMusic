package com.github.composemusic.route.drawer.playback

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.composemusic.R
import com.github.composemusic.bean.song.SongMediaBean
import com.github.composemusic.tool.CustomAlertDialog
import com.github.composemusic.tool.Loading
import com.github.composemusic.ui.theme.ComposeMusicTheme
import com.github.composemusic.ui.theme.green400
import com.github.composemusic.ui.theme.red100
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlaybackPage(
    viewModel: PlaybackViewModel = hiltViewModel(),
    onSongItem: (Long) -> Unit,
    onBack: () -> Unit
) {
    //val permissionState = rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val scaffoldState = rememberScaffoldState()
    val value = viewModel.uiState.value

    LaunchedEffect(scaffoldState.snackbarHostState) {
        viewModel.eventFlow.collectLatest {
            when (it) {
                is PlayBackState.Message -> {
                    scaffoldState.snackbarHostState.showSnackbar(it.msg)
                }

                is PlayBackState.Authorized -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = it.msg,
                        actionLabel = "To authorize"
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(PlayBackEvent.ApplicationPermission)
                    }
                }

                is PlayBackState.Permission -> {
//                    permissionState.launchPermissionRequest()
//                    when(permissionState.status){
//                        is PermissionStatus.Granted->{
//                            //授权权限
//                            APP.writePermissionState = true
//                        }
//
//                        is PermissionStatus.Denied->{
//                            //拒绝权限
//                            APP.writePermissionState = false
//                        }
//                    }
                }
            }
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
        Surface(
            color = ComposeMusicTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(20.dp)
            ) {
                stickyHeader{
                    TopTitleBar(title = "播放列表", onBack = onBack)
                }
                if (viewModel.playlist.isEmpty()) { item { Loading() } }
                itemsIndexed(viewModel.playlist) { index, item ->
                    val state = rememberDismissState(
                        confirmStateChange = {
                            when (it) {
                                DismissValue.DismissedToEnd -> {
                                    //从左到右-下载
                                    viewModel.onEvent(PlayBackEvent.Download(item))
                                }

                                DismissValue.DismissedToStart -> {
                                    //从右到左-删除
                                    viewModel.onEvent(PlayBackEvent.DeleteItem(item))
                                }

                                DismissValue.Default -> {

                                }
                            }
                            true
                        }
                    )
                    val rotate by animateFloatAsState(
                        targetValue = if (state.targetValue == DismissValue.Default)
                            0f
                        else
                            -180f,
                        label = "Rotate"
                    )
                    /**滑动到末端自动回弹*/
                    if (state.currentValue != DismissValue.Default) {
                        LaunchedEffect(Unit) {
                            state.reset()
                        }
                    }
                    SwipeToDismiss(
                        state = state,
                        background = {
                            val color = when (state.dismissDirection) {
                                DismissDirection.StartToEnd -> {
                                    //从左到右-下载
                                    green400
                                }

                                DismissDirection.EndToStart -> {
                                    //从右到左-删除
                                    red100
                                }

                                else -> {
                                    Color.Transparent
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 20.dp, end = 20.dp)
                                    .background(color = color)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "删除",
                                    tint = ComposeMusicTheme.colors.background,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.CenterEnd)
                                        .graphicsLayer {
                                            rotationZ = rotate
                                        }
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_dwonload),
                                    contentDescription = "下载",
                                    tint = ComposeMusicTheme.colors.background,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.CenterStart)
                                        .graphicsLayer {
                                            rotationZ = rotate
                                        }
                                )
                            }
                        }
                    ) {
                        SongsItem(
                            order = index + 1,
                            bean = item,
                            onSongItem = {
                                viewModel.onEvent(PlayBackEvent.PlayItem(index))
                                onSongItem(it)
                            }
                        )
                    }
                }
            }
            CustomAlertDialog(
                visibility = value.isVisibility,
                title = value.title,
                content = value.content,
                confirmText = value.confirmBtn,
                cancelText = value.cancelBtn,
                onConfirm = { viewModel.onEvent(PlayBackEvent.ConfirmDelete) },
                onCancel = { viewModel.onEvent(PlayBackEvent.CancelDelete) },
                onDismiss = { viewModel.onEvent(PlayBackEvent.CancelDelete) }
            )
        }
    }
}

@Composable
private fun SongsItem(
    order: Int,
    bean: SongMediaBean,
    onSongItem: (Long) -> Unit,
) {
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .background(ComposeMusicTheme.colors.background)
        .clickable { onSongItem(bean.songID) }
    ) {
        val (orderRes, title, num, more) = createRefs()

        Text(
            text = "$order",
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
            color = ComposeMusicTheme.colors.textTitle,
            modifier = Modifier.constrainAs(orderRes) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            }
        )

        //歌曲名字
        Text(
            text = bean.songName,
            style = MaterialTheme.typography.body2,
            color = ComposeMusicTheme.colors.textTitle,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(orderRes.end, 10.dp)
                end.linkTo(more.start)
                width = Dimension.fillToConstraints
            }
        )

        //歌手名称
        Text(
            text = bean.artist,
            style = MaterialTheme.typography.caption,
            color = ComposeMusicTheme.colors.textContent,
            modifier = Modifier.constrainAs(num) {
                bottom.linkTo(parent.bottom)
                start.linkTo(orderRes.end, 10.dp)
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

@Composable
fun TopTitleBar(title: String, onBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(ComposeMusicTheme.colors.background)
            .padding(top = 16.dp)
    ) {
        androidx.compose.material3.Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "返回",
            tint = ComposeMusicTheme.colors.textTitle,
            modifier = Modifier
                .size(32.dp)
                .clickable { onBack() }
        )
        androidx.compose.material.Text(
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