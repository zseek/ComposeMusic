package com.github.composemusic.network

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

// 封装网络请求结果
sealed class RemoteResult<out T> {
    class Success<T>(val data: T) : RemoteResult<T>()
    class Error(val exception: Exception) : RemoteResult<Nothing>()
}

suspend fun <T> baseApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> T
): RemoteResult<T> {
    return withContext(dispatcher) {
        try {
            RemoteResult.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> RemoteResult.Error(Exception("无法连接到网络"))
                is HttpException -> {
                    RemoteResult.Error(Exception(throwable.errorBody))
                }
                else -> RemoteResult.Error(Exception(throwable.message))
            }
        }
    }
}

private val HttpException.errorBody: String?
    get() = try {
        this.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        null
    }
