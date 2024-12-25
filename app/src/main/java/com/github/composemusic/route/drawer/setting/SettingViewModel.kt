package com.github.composemusic.route.drawer.setting

import androidx.lifecycle.ViewModel
import com.github.composemusic.APP

class SettingViewModel : ViewModel() {
    val maxDownloadNum = 5
    val downloadPath = APP.context.getExternalFilesDir("MusicDownload")?.absolutePath ?: ""
}