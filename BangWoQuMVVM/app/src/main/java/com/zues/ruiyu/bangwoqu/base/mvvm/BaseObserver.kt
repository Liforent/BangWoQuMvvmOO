package com.zues.ruiyu.bangwoqu.base.mvvm

import androidx.lifecycle.MutableLiveData
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.https.BaseResponse
import com.zues.ruiyu.bangwoqu.base.ZLog

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 *@Author liforent
 *@create 2020/8/25 14:14
 */
class BaseObserver<T : BaseResponse<*>>(
    val liveData: MutableLiveData<T>,
    val loadState: MutableLiveData<State>,
    val respository: BaseRepository,
    val apiCode:Int?
) : Observer<T> {
    override fun onComplete() {
    }

    override fun onSubscribe(d: Disposable) {
        loadState.postValue(State(StateType.LOADING))
        respository.addSubscribe(d)
    }

    override fun onNext(response: T) {
        when (response.code) {
            Constant.SUCCESS -> {
                if (response.data is List<*>) {
                    if ((response.data as List<*>).isEmpty()) {
                        loadState.postValue(State(StateType.EMPTY))
                        liveData.postValue(response)
                        ZLog.d("StateType.EMPTY")
                        return
                    }
                }
                loadState.postValue(State(StateType.SUCCESS))
                liveData.postValue(response)
                ZLog.d("StateType.SUCCESS")

            }
            Constant.NOT_LOGIN -> {
                loadState.postValue(State(StateType.RE_LOGIN, msg = "请重新登录"))
                ZLog.d("StateType.RE_LOGIN")

            }
            else -> {
                ZLog.d("StateType.ERROR")
                if (response.msg != null) {
                    loadState.postValue(State(StateType.ERROR, msg = response.msg))
                } else {
                    loadState.postValue(
                        State(
                            StateType.ERROR,
                            msg = "请求失败code=${response.code}msg=null"
                        )
                    )
                }
                //要传回responsebody，方便一个页面有多个接口的时候定位到当前livedata（或者要多post回当前接口的code了）
                //并且要先传回loadState,再传回liveData。loadState默认会在showErro()里showSuccessCallback,
                //而liveData里，如有必要，在StateType为Error的时候，可以手动调用showErrorcallback。
               liveData.postValue(response)

            }
        }
    }

    override fun onError(e: Throwable) {
        ZLog.e(e.toString())
        if (e.toString().contains("ConnectException") || e.toString()
                .contains("UnknownHostException")
        ) {
            loadState.postValue(State(StateType.SYSTEM_ERROR, msg = "网络连接失败！($apiCode)"))
        } else if (e.toString().contains("Unauthorized")) {
            loadState.postValue(State(StateType.RE_LOGIN, msg = "请重新登录"))
            ZLog.d("StateType.RE_LOGIN")
        } else {
            loadState.postValue(State(StateType.SYSTEM_ERROR, msg = "系统繁忙请稍后再试($apiCode)"))
        }
    }

}