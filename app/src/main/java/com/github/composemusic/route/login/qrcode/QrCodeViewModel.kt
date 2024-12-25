package com.github.composemusic.route.login.qrcode

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.composemusic.APP
import com.github.composemusic.bean.qrcode.QRCodeCookieBean
import com.github.composemusic.network.MusicApiService
import com.github.composemusic.network.RemoteResult
import com.github.composemusic.network.baseApiCall
import com.github.composemusic.parm.Constants
import com.github.composemusic.tool.SharedPreferencesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class QrCodeViewModel @Inject constructor(private val service: MusicApiService) : ViewModel() {
    /**
     * 轮询此接口可获取二维码扫码状态,
     * 800 为二维码过期,
     * 801 为等待扫码,
     * 802 为待确认,
     * 803 为授权登录成功(803 状态码下会返回 cookies),
     * 如扫码后返回502,则需加上noCookie参数,如&noCookie=true*/
    enum class QRCodeStatus(val status: Int) {
        Expire(800), Waiting(801), Confirm(802), Success(803), NoCookie(502)
    }

    private val _bitmapStatus = mutableStateOf(QRCodeLoginUIStatus(bitmap = null))
    val bitmapStatus: State<QRCodeLoginUIStatus> = _bitmapStatus

    private var unikey: String = ""
    private var count = 10

    private val _eventFlow = MutableSharedFlow<QRCodeLoginStatus>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var scheduler: ScheduledExecutorService =
        Executors.newScheduledThreadPool(1) // 创建一个线程池,用于定时任务

    private val timerTask = Runnable {
        if (count > 0) {
            viewModelScope.launch {
                if (unikey.isNotEmpty()) {
                    getQRCodeLoginStatus(unikey)
                }
            }
        } else {
            count = 10
            scheduler.shutdown() // 关闭线程池
        }
        count--
    }

    init {
        viewModelScope.launch {
            exeQRCode()
            scheduler.scheduleWithFixedDelay(
                timerTask,
                500,
                3000,
                TimeUnit.MILLISECONDS
            ) // 使用线程池执行定时任务
        }
    }

    private suspend fun exeQRCode() {
        getQRCodeLoginKey()
    }

    private suspend fun onEvent(bean: QRCodeCookieBean) {
        when (bean.code) {
            //二维码过期
            QRCodeStatus.Expire.status -> {
                _eventFlow.emit(QRCodeLoginStatus.Expire(bean.message))
//                qrCodeTimer.cancel()
                scheduler.shutdown()
            }
            //等待扫描
            QRCodeStatus.Waiting.status -> {
                _eventFlow.emit(QRCodeLoginStatus.Waiting(bean.message))
            }
            //以扫码，待授权
            QRCodeStatus.Confirm.status -> _eventFlow.emit(QRCodeLoginStatus.Confirm(bean.message))

            QRCodeStatus.Success.status -> {
                //授权成功,将cookie保存到本地，以备下次可以免去登录，避免重复登录，被网易云盾监测
                SharedPreferencesUtil.instance.putValue(APP.context, Constants.Cookie, bean.cookie)
                _eventFlow.emit(QRCodeLoginStatus.Success(bean.message))
//                qrCodeTimer.cancel()
                scheduler.shutdown()
            }
            //加上noCookie参数,如&noCookie=true
            QRCodeStatus.NoCookie.status -> {
                _eventFlow.emit(QRCodeLoginStatus.NoCookie(bean.message))
//                qrCodeTimer.cancel()
                scheduler.shutdown()
            }
        }
    }

    fun refreshQRCode() {
        viewModelScope.launch {
            count = 10
            exeQRCode()
            _bitmapStatus.value = bitmapStatus.value.copy(refresh = !_bitmapStatus.value.refresh)
            _eventFlow.emit(QRCodeLoginStatus.RefreshQRCode)
            if (scheduler.isShutdown) {
                scheduler = Executors.newScheduledThreadPool(1)
                scheduler.scheduleWithFixedDelay(timerTask, 500, 3000, TimeUnit.MILLISECONDS)
            }
        }
    }

    /**获取QRCODE KEY*/
    private suspend fun getQRCodeLoginKey() {
        val response = baseApiCall { service.getQRCodeLoginKey() }
        when (response) {
            is RemoteResult.Success -> {
                if (response.data.code == 200) {
                    unikey = response.data.data.unikey
                    getQRCodeLoginImg(unikey)
                } else {
                    count = 10
//                    qrCodeTimer.cancel()
                    scheduler.shutdown()
                    _eventFlow.emit(QRCodeLoginStatus.NetworkFailed("The error code is ${response.data.code}"))
                }
            }

            is RemoteResult.Error -> {
                count = 10
//                qrCodeTimer.cancel()
                scheduler.shutdown()
                _eventFlow.emit(QRCodeLoginStatus.NetworkFailed(response.exception.message.toString()))
            }
        }
    }

    /**根据QRCODE KEY获取二维码*/
    private suspend fun getQRCodeLoginImg(key: String) {
        val response = baseApiCall { service.getQRCodeLoginImg(key) }
        when (response) {
            is RemoteResult.Success -> {
                if (response.data.code == 200) {
                    val bitmap = base64ToBitmap(response.data.data.qrimg)
                    _bitmapStatus.value = bitmapStatus.value.copy(bitmap = bitmap)
                } else {
                    count = 10
//                    qrCodeTimer.cancel()
                    scheduler.shutdown()
                    _eventFlow.emit(QRCodeLoginStatus.NetworkFailed("The error code is ${response.data.code}"))
                }
            }

            is RemoteResult.Error -> {
                count = 10
//                qrCodeTimer.cancel()
                scheduler.shutdown()
                _eventFlow.emit(QRCodeLoginStatus.NetworkFailed(response.exception.message.toString()))
            }
        }
    }

    /**获取二维码状态*/
    private suspend fun getQRCodeLoginStatus(key: String) {
        val response = baseApiCall { service.getQRCodeLoginStatus(key) }
        when (response) {
            is RemoteResult.Success -> {
                onEvent(response.data)
            }

            is RemoteResult.Error -> {
                count = 10
                scheduler.shutdown()
                _eventFlow.emit(QRCodeLoginStatus.NetworkFailed(response.exception.message.toString()))
            }
        }
    }

    private fun base64ToBitmap(qrCodeUrl: String): Bitmap {
        val decode: ByteArray = Base64.decode(qrCodeUrl.split(",")[1], Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decode, 0, decode.size)
    }
}