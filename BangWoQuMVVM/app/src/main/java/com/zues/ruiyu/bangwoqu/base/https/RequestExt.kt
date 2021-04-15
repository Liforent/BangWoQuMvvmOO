package com.zues.ruiyu.bangwoqu.base.https

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseViewModel
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseRepository
import com.zues.ruiyu.bangwoqu.base.mvvm.State
import com.zues.ruiyu.bangwoqu.base.mvvm.StateType
import com.zues.ruiyu.bangwoqu.base.ZLog
import kotlinx.coroutines.launch

/**
 *
 *
 *
 *
 */
fun <T : BaseRepository> BaseViewModel<T>.initiateRequest(
    block: suspend () -> Unit,
    loadState: MutableLiveData<State>,
    apiCode: Int
) {
    viewModelScope.launch {
        runCatching {
            loadState.postValue(State(StateType.LOADING))
            block()
            ZLog.d("onSuccess")
        }.onFailure {
            NetExceptionHandle.handleException(it, loadState, apiCode)
        }
    }

}

fun <T> BaseResponse<T>.onResponse(
    loadState: MutableLiveData<State>,
    onSuccess: () -> Unit
) {
    when (this.code) {
        Constant.SUCCESS -> {
            if (this.data is List<*>) {
                if ((this.data as List<*>).isEmpty()) {
                    loadState.postValue(State(StateType.EMPTY))
                    ZLog.d("StateType.EMPTY")
                    return
                }
            }
            loadState.postValue(State(StateType.SUCCESS))
            onSuccess()
            ZLog.d("StateType.SUCCESS,data:"+this.data.toString())

        }
        Constant.NOT_LOGIN -> {
            loadState.postValue(State(StateType.RE_LOGIN, msg = "请重新登录"))
            ZLog.d("StateType.RE_LOGIN")

        }
        else -> {
            ZLog.e("StateType.ERROR" + this.msg?.toString())
            if (this.msg != null) {
                loadState.postValue(State(StateType.ERROR, msg = this.msg))
            } else {
                loadState.postValue(
                    State(
                        StateType.ERROR,
                        msg = "请求失败code=${this.code}msg=null"
                    )
                )
            }
        }
    }
}


fun <T> BaseResponse<T>.onResponse(
    loadState: MutableLiveData<State>,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    when (this.code) {
        Constant.SUCCESS -> {
            if (this.data is List<*>) {
                if ((this.data as List<*>).isEmpty()) {
                    loadState.postValue(State(StateType.EMPTY))
                    ZLog.d("StateType.EMPTY")
                    return
                }
            }
            loadState.postValue(State(StateType.SUCCESS))
            onSuccess()
            ZLog.d("StateType.SUCCESS,data:"+this.data.toString())

        }
        Constant.NOT_LOGIN -> {
            loadState.postValue(State(StateType.RE_LOGIN, msg = "请重新登录"))
            ZLog.d("StateType.RE_LOGIN")

        }
        else -> {
            onError()
            ZLog.e("StateType.ERROR" + this.msg?.toString())
            if (this.msg != null) {
                loadState.postValue(State(StateType.ERROR, msg = this.msg))
            } else {
                loadState.postValue(
                    State(
                        StateType.ERROR,
                        msg = "请求失败code=${this.code}msg=null"
                    )
                )
            }
        }
    }


}

fun String.getApiCode(): String {
    return if (this.length >= 6) {
        this.substring(this.length - 5, this.length-1)
    } else {
        ""
    }

}