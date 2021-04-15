package com.zues.ruiyu.bangwoqu.module.setting

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.zues.ruiyu.bangwoqu.base.https.ApiList
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseViewModel
import com.zues.ruiyu.bangwoqu.base.https.BaseResponse
import com.zues.ruiyu.bangwoqu.base.https.EmptyResponse
import com.zues.ruiyu.bangwoqu.base.https.initiateRequest
import com.zues.ruiyu.bangwoqu.module.setting.data.SettingPswModel

/**
 *@Author liforent
 *@create 2020/9/8 23:21
 */
class SettingPswViewModel(application: Application) :
    BaseViewModel<SettingRepository>(application) {
    val mSettingPswData: MutableLiveData<BaseResponse<EmptyResponse>> = MutableLiveData()

    fun setPsw(
        token: String,
        model: SettingPswModel
    ) {
        initiateRequest({
            mSettingPswData.value = mRepository.setPsw(
                token,
                model
            )
        }, loadState, ApiList.SET_PSW)

    }
}