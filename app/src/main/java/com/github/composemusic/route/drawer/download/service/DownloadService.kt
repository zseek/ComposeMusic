package com.github.composemusic.route.drawer.download.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.annotation.RequiresApi
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.download.DownloadTaskListener
import com.arialyy.aria.core.task.DownloadTask
import com.github.composemusic.APP
import com.github.composemusic.R
import com.github.composemusic.parm.Constants
import com.github.composemusic.route.drawer.download.notification.DownloadNotification
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DownloadService : Service(), DownloadTaskListener {
    private lateinit var notification: DownloadNotification
    private var isForegroundSuc = false
    private var timerFlag = false
    private val FOREGROUND_NOTIFY_ID = 1
    private lateinit var listener: DownloadListener

    private var notificationID = 100

    private var map: Map<String, Int> = emptyMap()

    override fun onBind(p0: Intent?): IBinder = DownloadBinder()

    inner class DownloadBinder : Binder() {
        val service: DownloadService
            get() = this@DownloadService
    }

    override fun onCreate() {
        super.onCreate()
        initAria()
        initNotification()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val url = intent.getStringExtra(Constants.DownloadURL) ?: ""
            val path = intent.getStringExtra(Constants.DownloadPath) ?: ""
            val cover = intent.getStringExtra(Constants.DownloadCover) ?: ""
            val name = intent.getStringExtra(Constants.DownloadName) ?: "Unknown"
            val taskID = Aria.download(this)
                .load(url)
                .setFilePath(path)
                .create()
            if (taskID > 0L) {
                notificationID++
                map += url to notificationID
                startForeground(name, cover)
            }
            /**
             * 如果明确服务一定是前台服务，在 Android 8.0 以后可以调用 startForegroundService，
             * 它和 startService 的区别是它隐含了一个承诺，必须在服务中尽快调用 startForeground，否则 5s 后服务将停止，且会触发 ANR。*/
            if (!timerFlag) {
                timerFlag = true
                object : CountDownTimer(4500L, 4500L) {
                    override fun onTick(p0: Long) {

                    }

                    override fun onFinish() {
                        if (!isForegroundSuc) {
                            /**如果4.5s后没有执行相关操作，则停止服务*/
                            stopForeground(STOP_FOREGROUND_DETACH)
                            stopSelf()
                        }
                    }
                }.start()
            }
        }
        return super.onStartCommand(intent, flags, startId)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForeground(name: String, cover: String) {
        if (!isForegroundSuc) {
            getBitmap(
                url = cover,
                onSuccess = {
                    startForeground(
                        FOREGROUND_NOTIFY_ID,
                        notification.createNotification(notificationID, name, it)
                    )
                    isForegroundSuc = true
                },
                onError = {
                    val bitmap = BitmapFactory.decodeResource(
                        APP.context.resources,
                        R.drawable.composemusic_logo
                    )
                    startForeground(
                        FOREGROUND_NOTIFY_ID,
                        notification.createNotification(notificationID, name, bitmap)
                    )
                    isForegroundSuc = true
                }
            )
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getBitmap(
        url: String?,
        onSuccess: (Bitmap) -> Unit,
        onError: (String) -> Unit
    ) {
        var bitmap: Bitmap? = null
        val scope = GlobalScope.launch(Dispatchers.Main) {
            val request = ImageRequest.Builder(context = APP.context)
                .data(url)
                .allowHardware(false)
                .build()
            val result = APP.context.imageLoader.execute(request)
            if (result is SuccessResult) {
                bitmap = (result.drawable as BitmapDrawable).bitmap
            } else {
                cancel("Error Request")
            }
        }
        scope.invokeOnCompletion {
            bitmap?.let { bitmap ->
                onSuccess(bitmap)
            } ?: it?.let {
                onError(it.message.toString())
            } ?: onError("Unknown Exception")
        }
    }

    private fun initAria() {
        Aria.download(this).register()
        Aria.get(this).downloadConfig
            .setMaxTaskNum(3)
            .setUseBlock(true)
            .setConvertSpeed(true)
            .setUpdateInterval(3000L)
    }

    private fun initNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = DownloadNotification(APP.context)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Aria.download(this).unRegister()
        isForegroundSuc = false
        timerFlag = false
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    fun setDownloadListener(listener: DownloadListener) {
        this.listener = listener
    }

    private fun onDownloadListener(task: DownloadTask, msg: String) {
        if (this::listener.isInitialized) {
            listener.onDownloadState(task, msg)
        }
    }


    /**任务预加载*/
    override fun onPre(task: DownloadTask?) {
        if (task != null) {
            onDownloadListener(task, "")
        }
    }

    /**任务预加载完成*/
    override fun onTaskPre(task: DownloadTask?) {
        if (task != null) {
            onDownloadListener(task, "")
        }
    }

    /**等待中*/
    override fun onWait(task: DownloadTask?) {
        if (task != null) {
            onDownloadListener(task, "")
        }
    }

    /**开始下载*/
    override fun onTaskStart(task: DownloadTask?) {
        if (task != null) {
            onDownloadListener(task, "")
        }
    }

    /**下载暂停*/
    override fun onTaskStop(task: DownloadTask?) {
        if (task != null) {
            onDownloadListener(task, "")
        }
    }

    /**下载恢复*/
    override fun onTaskResume(task: DownloadTask?) {
        if (task != null) {
            onDownloadListener(task, "")
        }
    }

    /**下载中*/
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTaskRunning(task: DownloadTask?) {
        if (task != null) {
            task.convertFileSize
            val progress = (task.currentProgress * 100 / task.fileSize).toInt()
            val id = searchNotificationID(task.entity.url)
            notification.setProgress(id, progress)
            onDownloadListener(task, "")
        }
    }

    /**任务不支持断点*/
    override fun onNoSupportBreakPoint(task: DownloadTask?) {
        if (task != null) {
            onDownloadListener(task, "")
        }
    }

    /**下载完成*/
    override fun onTaskComplete(task: DownloadTask?) {
        if (task != null) {
            val completeList = Aria.download(this).allCompleteTask
            val unCompleteList = Aria.download(this).allNotCompleteTask
            if (completeList != null && unCompleteList != null && completeList.isNotEmpty() && unCompleteList.isEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    stopForeground(Service.STOP_FOREGROUND_DETACH)
                    isForegroundSuc = false
                }
                //下载任务全部完成，结束service
                stopSelf()
            }
            onDownloadListener(task, "")
        }
    }

    /**下载失败*/
    override fun onTaskFail(task: DownloadTask?, e: Exception?) {
        if (task != null) {
            onDownloadListener(task, e?.message.toString())
        }
    }

    /**取消下载*/
    override fun onTaskCancel(task: DownloadTask?) {
        if (task != null) {
            onDownloadListener(task, "")
        }
    }

    private fun searchNotificationID(url: String): Int {
        if (map.isEmpty()) return 0
        map.forEach {
            if (it.key == url) return it.value
        }
        return 0
    }
}