package com.zues.ruiyu.bangwoqu.base.https

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonParseException
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.base.mvvm.State
import com.zues.ruiyu.bangwoqu.base.mvvm.StateType
import retrofit2.HttpException
import java.lang.NullPointerException
import java.net.ConnectException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

/**
 *
 * Description:
 *      0解析异常或者空指针等异常，-1服务器异常，1其他异常
 *
 *
 */
object NetExceptionHandle {
    private const val UNAUTHORIZED = 401
    private const val FORBIDDEN = 403
    private const val NOT_FOUND = 404
    private const val REQUEST_TIMEOUT = 408
    private const val INTERNAL_SERVER_ERROR = 500
    private const val BAD_GATEWAY = 502
    private const val SERVICE_UNAVAILABLE = 503
    private const val GATEWAY_TIMEOUT = 504


    fun handleException(e: Throwable?, loadState: MutableLiveData<State>, apiCode: Int) {
        e?.let {
            ZLog.e("Throwable:$e")
            if (it is HttpException) {
                when (it.code()) {
                    UNAUTHORIZED -> {
                        loadState.postValue(State(StateType.RE_LOGIN, msg = "请重新登录($apiCode)"))
                        ZLog.d("StateType.RE_LOGIN")
                    }
                    else -> {
                        loadState.postValue(State(StateType.SYSTEM_ERROR, msg = "系统繁忙请稍后再试(-1，$apiCode)"))
                    }
                }
            } else if (it is ConnectException || it is UnknownHostException) {
                loadState.postValue(State(StateType.SYSTEM_ERROR, msg = "网络连接失败！($apiCode)"))
            } else if (it is SSLHandshakeException) {
                loadState.postValue(State(StateType.SYSTEM_ERROR, msg = "证书验证失败！($apiCode)"))
            } else if (it is JsonParseException || it is NullPointerException) {
                loadState.postValue(State(StateType.SYSTEM_ERROR, msg = "系统繁忙请稍后再试！(0，$apiCode)"))
            } else {
                loadState.postValue(State(StateType.SYSTEM_ERROR, msg = "系统繁忙请稍后再试！(1，$apiCode)"))
            }
        }
    }
}