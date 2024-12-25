package com.github.composemusic.route.drawer.about

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.composemusic.APP
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

fun getAppVersion(): String {
    val manager = APP.context.packageManager
    var version = ""
    try {
        val info = manager.getPackageInfo(APP.context.packageName, 0)
        version = info.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        version = e.message.toString()
    }
    return version
}

// AboutInfo 数据类
data class AboutInfo(
    val appName: String,
    val version: String,
    val description: String,
    val developerName: String,
    val developerEmail: String
)

class AboutViewModel : ViewModel() {
    private val _aboutInfo = MutableStateFlow(
        AboutInfo(
            appName = "ComposeMusic",
            version = getAppVersion(),
            description = "ComposeMusic 是一个提供丰富音乐体验的应用，支持多种功能和高质量音频播放。",
            developerName = "github.com/zseek",
            developerEmail = ""
        )
    )

    val aboutInfo: StateFlow<AboutInfo> = _aboutInfo

    // 如果需要动态加载数据，可以在这里执行
    fun loadAboutInfo() {
        viewModelScope.launch {
            // 模拟从网络或数据库加载数据
            _aboutInfo.value = _aboutInfo.value.copy(
                // 更新或加载需要的数据
            )
        }
    }
}
