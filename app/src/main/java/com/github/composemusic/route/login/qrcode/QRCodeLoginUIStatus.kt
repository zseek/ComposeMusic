package com.github.composemusic.route.login.qrcode

import android.graphics.Bitmap

data class QRCodeLoginUIStatus(
    var bitmap: Bitmap?,
    var refresh: Boolean = false
)