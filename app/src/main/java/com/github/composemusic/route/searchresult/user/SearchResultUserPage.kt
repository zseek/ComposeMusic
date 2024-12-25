package com.github.composemusic.route.searchresult.user

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
import com.github.composemusic.route.searchresult.SearchResultUserItem
import com.github.composemusic.tool.Loading
import com.github.composemusic.tool.LoadingFailed
import com.github.composemusic.ui.theme.ComposeMusicTheme

@Composable
fun SearchResultUserList(
    keyword:String,
    viewModel: SearchResultUserViewModel = hiltViewModel(),
    onUser:(Long)->Unit
){
    val users = viewModel.getSearchUserResult(keyword).collectAsLazyPagingItems()
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp)
            .background(ComposeMusicTheme.colors.background),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        when(users.loadState.refresh){
            is LoadState.Loading-> { item { Loading() } }
            is LoadState.Error-> { item { LoadingFailed{ users.retry() } } }
            else ->{}
        }
        items(items = users){user->
            if (user != null) {
                SearchResultUserItem(
                    cover = user.avatarUrl,
                    nickname = user.nickname,
                    isFollow = user.followed,
                    onClick = { onUser(user.userId) }
                )
            }
        }
        when(users.loadState.append){
            is LoadState.Loading-> { item { Loading() } }
            else ->{}
        }
    }
}