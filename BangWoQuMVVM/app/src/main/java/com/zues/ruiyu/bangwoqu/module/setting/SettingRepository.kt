package com.zues.ruiyu.bangwoqu.module.setting

import androidx.lifecycle.MutableLiveData
import com.zues.ruiyu.bangwoqu.base.https.BaseResponse
import com.zues.ruiyu.bangwoqu.base.https.EmptyResponse
import com.zues.ruiyu.bangwoqu.base.mvvm.ApiRepository
import com.zues.ruiyu.bangwoqu.base.mvvm.State
import com.zues.ruiyu.bangwoqu.module.setting.data.SettingPswModel

/**
 *@Author liforent
 *@create 2020/9/8 23:15
 */
class SettingRepository(val loadState: MutableLiveData<State>): ApiRepository() {
   suspend fun setPsw(
        token: String,
        model: SettingPswModel
    ):BaseResponse<EmptyResponse> {
     return   apiService.setPsw(token,model)
    }
}