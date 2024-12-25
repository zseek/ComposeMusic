package com.github.composemusic.route.search

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.composemusic.R
import com.github.composemusic.bean.search.HotSearch
import com.github.composemusic.bean.search.SearchRecordBean
import com.github.composemusic.tool.CustomAlertDialog
import com.github.composemusic.tool.SearchBar
import com.github.composemusic.ui.theme.ComposeMusicTheme
import com.google.accompanist.insets.statusBarsPadding

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchPage(
    viewModel: SearchViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSearch: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val value = viewModel.uiStatus.value
    val headers = remember { SearchHeaders.values() }
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(scaffoldState.snackbarHostState) {
        viewModel.eventFlow.collect {
            when (it) {
                is SearchStatus.SearchEmpty -> {
                    scaffoldState.snackbarHostState.showSnackbar(it.msg)
                }
                is SearchStatus.SearchFailed -> {
                    scaffoldState.snackbarHostState.showSnackbar(it.msg)
                }
                is SearchStatus.SearchSuccess -> {
                    onSearch(it.msg)
                }
                is SearchStatus.Clear -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = it.msg,
                        actionLabel = "取消"
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        //执行撤回操作
                        viewModel.onEvent(SearchEvent.Withdraw)
                    }
                }
                is SearchStatus.Withdraw -> scaffoldState.snackbarHostState.showSnackbar(it.msg)
                is SearchStatus.Message -> scaffoldState.snackbarHostState.showSnackbar(it.msg)

            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .background(ComposeMusicTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        snackbarHost = {
            SnackbarHost(hostState = it) { data ->
                Snackbar(
                    snackbarData = data,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }
    ) {
        CustomAlertDialog(
            visibility = value.isShowDialog,
            title = viewModel.title,
            content = viewModel.content,
            confirmText = viewModel.confirm,
            cancelText = viewModel.cancel,
            onConfirm = { viewModel.onEvent(SearchEvent.ConfirmClear) },
            onCancel = { viewModel.onEvent(SearchEvent.CancelClear) },
            onDismiss = { viewModel.onEvent(SearchEvent.CancelClear) }
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(ComposeMusicTheme.colors.background)
                .padding(top = 10.dp, start = 20.dp, end = 20.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            SearchBar(
                focusManager = focusManager,
                value = value.keywords,
                hint = value.default,
                onValueChange = { viewModel.onEvent(SearchEvent.ChangeKey(it)) },
                onSearch = { viewModel.onEvent(SearchEvent.Search(value.keywords)) },
                onBack = onBack
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                //热搜&历史搜索记录
                LazyColumn(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
                ) {
                    headers.forEach { header ->
                        when (header) {
                            SearchHeaders.TopSearch -> {
                                item {
                                    if (value.hots.isNotEmpty()) {
                                        FindingStickyHeader(
                                            header = header.header,
                                            isShowClear = false,
                                            onClear = {}
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }
                                }
                            }
                            SearchHeaders.RecentSearch -> {
                                item {
                                    if (value.isShowClear) {
                                        FindingStickyHeader(
                                            header = header.header,
                                            isShowClear = true
                                        ) {
                                            /**清除历史搜索 + 记录点击事件处理*/
                                            viewModel.onEvent(SearchEvent.Clear)
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }
                                }
                            }
                        }
                        when (header) {
                            SearchHeaders.TopSearch -> {
                                item {
                                    if (value.hots.isNotEmpty()) {
                                        HotSearchList(value.hots) {
                                            viewModel.onEvent(SearchEvent.Search(it))
                                        }
                                        Spacer(modifier = Modifier.height(20.dp))
                                    }
                                }
                            }
                            SearchHeaders.RecentSearch -> {
                                item {
                                    if (value.isShowClear) {
                                        RecentSearchList(value.histories) {
                                            viewModel.onEvent(SearchEvent.Search(it))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //搜索建议界面(输入关键字时出现)
                androidx.compose.animation.AnimatedVisibility(visible = !value.isEmptySuggestions) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ComposeMusicTheme.colors.background)
                    ) {
                        value.suggestions?.order?.forEach { title ->
                            // 标题
                            item {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.body1,
                                    color = Color.Blue,
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            // 内容
                            when (title) {
                                SearchSuggestionHeaders.Albums.title -> {
                                    if (value.suggestions.albums.isNotEmpty()) {
                                        items(value.suggestions.albums.size) {
                                            SearchSuggestionItem(value.suggestions.albums[it].name) { key ->
                                                viewModel.onEvent(SearchEvent.Search(key))
                                            }
                                            if (it < value.suggestions.albums.size - 1)
                                                Spacer(modifier = Modifier.height(5.dp))
                                        }
                                    }
                                }
                                SearchSuggestionHeaders.Artists.title -> {
                                    if (value.suggestions.artists.isNotEmpty()) {
                                        items(value.suggestions.artists.size) {
                                            SearchSuggestionItem(value.suggestions.artists[it].name) { key ->
                                                viewModel.onEvent(SearchEvent.Search(key))
                                            }
                                            if (it < value.suggestions.artists.size - 1)
                                                Spacer(modifier = Modifier.height(5.dp))
                                        }
                                    }
                                }
                                SearchSuggestionHeaders.Songs.title -> {
                                    if (value.suggestions.songs.isNotEmpty()) {
                                        items(value.suggestions.songs.size) {
                                            SearchSuggestionItem(value.suggestions.songs[it].name) { key ->
                                                viewModel.onEvent(SearchEvent.Search(key))
                                            }
                                            if (it < value.suggestions.songs.size - 1)
                                                Spacer(modifier = Modifier.height(5.dp))
                                        }
                                    }
                                }
                                SearchSuggestionHeaders.Playlists.title -> {
                                    if (value.suggestions.playlists.isNotEmpty()) {
                                        items(value.suggestions.playlists.size) {
                                            SearchSuggestionItem(value.suggestions.playlists[it].name) { key ->
                                                viewModel.onEvent(SearchEvent.Search(key))
                                            }
                                            if (it < value.suggestions.playlists.size - 1)
                                                Spacer(modifier = Modifier.height(5.dp))
                                        }
                                    }
                                }
                            }
                            item { Spacer(modifier = Modifier.height(15.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HotSearchList(hots: List<HotSearch>, onSearch: (String) -> Unit) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
            .padding(5.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        items(hots.size) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    painter = painterResource(id = R.drawable.icon_hot),
                    contentDescription = "搜索记录",
                    tint = ComposeMusicTheme.colors.unselectIcon,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = hots[it].first,
                    color = ComposeMusicTheme.colors.textTitle,
                    style = MaterialTheme.typography.subtitle2.copy(fontFamily = FontFamily.Default),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .clickable { onSearch(hots[it].first) }
                )
            }
        }
    }
}

@Composable
private fun RecentSearchList(histories: List<SearchRecordBean>, onSearch: (String) -> Unit) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
            .padding(5.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        items(histories.size) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_recent_search),
                    contentDescription = "搜索记录",
                    tint = ComposeMusicTheme.colors.unselectIcon,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = histories[it].keyword,
                    color = ComposeMusicTheme.colors.textTitle,
                    style = MaterialTheme.typography.subtitle2.copy(fontFamily = FontFamily.Default),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .clickable { onSearch(histories[it].keyword) }
                )
            }
        }
    }
}

@Composable
private fun SearchSuggestionItem(suggestion: String, onSearch: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSearch(suggestion) }
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "搜索建议",
            tint = ComposeMusicTheme.colors.defaultIcon,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = suggestion,
            style = MaterialTheme.typography.subtitle2,
            color = ComposeMusicTheme.colors.textContent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FindingStickyHeader(
    header: String,
    isShow: Boolean = true,
    isShowClear: Boolean = false,
    style: TextStyle = MaterialTheme.typography.h6,
    onClear: () -> Unit
) {
    if (isShow) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = header,
                style = style,
                color = ComposeMusicTheme.colors.textTitle,
                modifier = Modifier.weight(1f)
            )
            if (isShowClear) {
                Text(
                    text = stringResource(id = R.string.clear),
                    style = MaterialTheme.typography.subtitle2,
                    color = ComposeMusicTheme.colors.highlightColor,
                    modifier = Modifier.clickable { onClear() }
                )
            }
        }
    }
}

private enum class SearchHeaders(val header: String) {
    TopSearch("热门搜索"),
    RecentSearch("搜索历史")
}

private enum class SearchSuggestionHeaders(val title: String) {
    Albums("专辑"),
    Artists("艺术家"),
    Songs("歌曲"),
    Playlists("歌单")
}



