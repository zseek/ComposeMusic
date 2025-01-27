package com.github.composemusic.route.drawer.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.composemusic.R
import com.github.composemusic.route.drawer.recent.TopTitleBar
import com.github.composemusic.ui.theme.ComposeMusicTheme
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun SettingPage(
    viewModel: SettingViewModel = viewModel(),
    onBack: () -> Unit
) {
    val tabs = remember { SettingTab.values().map { it.tab } }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(ComposeMusicTheme.colors.grayBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
    ) {
        item {
            TopTitleBar(onBack = onBack, title = "设置")
        }
        items(tabs.size) { index ->
            when (tabs[index]) {
                SettingTab.DownloadNum.tab -> {
                    SettingItem(
                        key = tabs[index],
                        value = "${viewModel.maxDownloadNum}"
                    )
                }
                SettingTab.DownloadPath.tab -> {
                    SettingItem(
                        key = tabs[index],
                        value = viewModel.downloadPath
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingItem(@StringRes key: Int, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ComposeMusicTheme.colors.background)
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(id = key),
            color = ComposeMusicTheme.colors.textTitle,
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = value,
            color = ComposeMusicTheme.colors.textContent,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

private enum class SettingTab(@StringRes val tab: Int) {
    DownloadNum(R.string.download_number),
    DownloadPath(R.string.download_path)
}