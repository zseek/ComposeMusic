package com.github.composemusic

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import com.github.composemusic.navigation.NavigationGraph
import com.github.composemusic.navigation.Screen
import com.github.composemusic.parm.Constants
import com.github.composemusic.route.musicplayer.service.MusicService
import com.github.composemusic.tool.SharedPreferencesUtil
import com.github.composemusic.ui.theme.ComposeMusicTheme
import com.github.composemusic.ui.theme.themeState
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class APP : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        var cookie: String = ""
        var userId: Long = 0L

        lateinit var permissions: MutableMap<String, Boolean>

        private var isRunning = false

        // 获取起始路径: 根据是否含有登录cookie
        fun getStartDestination(): String {
            return if (cookie.isEmpty()) {
                Screen.PasswordLoginPage.route
            } else {
                Screen.ContainerPage.route
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        cookie = SharedPreferencesUtil.instance.getValue(context, Constants.Cookie, "") as String
        userId = SharedPreferencesUtil.instance.getValue(context, Constants.UserId, 0L) as Long

        startMusicService()
    }

    private fun startMusicService() {
        if (!isRunning) {
            val intent = Intent(applicationContext, MusicService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            isRunning = true
        }
    }
}

//APP入口
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ComposeMusicApp() {
    ComposeMusicTheme(theme = themeState.value) {
        ProvideWindowInsets {
            val systemUiController = rememberSystemUiController()
            val navHostController = rememberNavController()
            SideEffect {
                systemUiController.setStatusBarColor(Color.Transparent, true)
            }
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) {
                NavigationGraph(
                    navHostController = navHostController,
                    startDistance = APP.getStartDestination()
                )
            }
        }
    }
}

/**申请多个权限*/
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ApplyForMultiPermissions(
    permissionState: MultiplePermissionsState,
    onPermission: (MutableMap<String, Boolean>) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> permissionState.launchMultiplePermissionRequest()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })
    val permissions: MutableMap<String, Boolean> = mutableMapOf()
    permissionState.permissions.forEach {
        permissions += when (it.status) {
            is PermissionStatus.Granted -> {
                it.permission to true
            }
            is PermissionStatus.Denied -> {
                it.permission to false
            }
        }
    }
    onPermission(permissions)
}

/**判断权限申请状态*/
fun checkPermission(permissions: MutableMap<String, Boolean>): PermissionApplyState {
    val count = permissions.filter { it.value }.size
    return when (count) {
        0 -> PermissionApplyState.ALlDenied
        permissions.size -> PermissionApplyState.AllAuthority
        else -> PermissionApplyState.PartAuthority
    }
}

/**申请单个权限*/
@OptIn(ExperimentalPermissionsApi::class)
fun applySingerPermission(permission: PermissionState) {
    permission.launchPermissionRequest()
    when (permission.status) {
        is PermissionStatus.Granted -> {
        }
        is PermissionStatus.Denied -> {
        }
    }
}

enum class PermissionApplyState {
    AllAuthority,
    PartAuthority,
    ALlDenied
}

