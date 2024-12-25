package com.github.composemusic.route.drawer.about

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.composemusic.R
import com.github.composemusic.route.drawer.recent.TopTitleBar
import com.github.composemusic.ui.theme.ComposeMusicTheme
import com.google.accompanist.insets.statusBarsPadding


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AboutPage(
    viewModel: AboutViewModel = AboutViewModel(),
    onBack: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val aboutInfo = viewModel.aboutInfo.collectAsState().value

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { hostState ->
            SnackbarHost(
                hostState = hostState,
                modifier = Modifier.navigationBarsPadding()
            ) { data ->
                Snackbar(snackbarData = data)
            }
        }
    ) {
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
            item { TopTitleBar("关于", onBack) }
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    painter = painterResource(id = R.drawable.composemusic_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(160.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            // 应用名称和版本
            item {
                Text(
                    text = aboutInfo.appName,
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "版本 ${aboutInfo.version}",
                    style = MaterialTheme.typography.body2,
                    color = ComposeMusicTheme.colors.textTitle
                )
            }

            // 应用描述
            item {
                Text(
                    text = aboutInfo.description,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // 开发者信息
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp,
                    backgroundColor = MaterialTheme.colors.surface
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "开发者",
                            style = MaterialTheme.typography.subtitle1,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = aboutInfo.developerName,
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "联系邮箱：${aboutInfo.developerEmail}",
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                }
            }

            // 隐私政策、服务条款和开源许可
            item {
                Divider(color = MaterialTheme.colors.onBackground.copy(alpha = 0.2f))
                TextButton(
                    onClick = { /* 点击跳转到隐私政策页面 */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("隐私政策", color = ComposeMusicTheme.colors.textTitle)
                }
                Divider(color = MaterialTheme.colors.onBackground.copy(alpha = 0.2f))
                TextButton(
                    onClick = { /* 点击跳转到服务条款页面 */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("服务条款", color = ComposeMusicTheme.colors.textTitle)
                }
                Divider(color = MaterialTheme.colors.onBackground.copy(alpha = 0.2f))
                TextButton(
                    onClick = { /* 点击跳转到开源许可页面 */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("开源许可", color = ComposeMusicTheme.colors.textTitle)
                }
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}





