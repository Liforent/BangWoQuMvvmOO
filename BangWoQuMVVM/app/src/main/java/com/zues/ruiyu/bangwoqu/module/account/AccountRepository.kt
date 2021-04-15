package com.zues.ruiyu.bangwoqu.module.account

import androidx.lifecycle.MutableLiveData
import com.zues.ruiyu.bangwoqu.base.https.BaseResponse
import com.zues.ruiyu.bangwoqu.base.https.EmptyResponse
import com.zues.ruiyu.bangwoqu.base.mvvm.ApiRepository
import com.zues.ruiyu.bangwoqu.base.mvvm.State
import com.zues.ruiyu.bangwoqu.module.account.data.*
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.UserInfoResponse

/**
 *@Author liforent
 *@create 2020/9/8 14:58
 */
class AccountRepository(val loadState: MutableLiveData<State>) : ApiRepository() {
    suspend fun login(
        model: LoginModel
    ): BaseResponse<AccountResponse> {
        return apiService.onLogin(model)
    }

    suspend fun register(
        model: RegisterModel
    ): BaseResponse<AccountResponse> {
        return apiService.onRegister(model)
    }

    suspend fun getVerificationCode(
        model: VerificationCodeModel
    ): BaseResponse<EmptyResponse> {
        return apiService.getVerificationCode(model)

    }


    suspend fun updateUserInfo(
        token: String,
        user_id: Long,
        real_name: String?,
        province: String?,
        city: String?,
        county: String?,
        town: String?,
        default_address: String?,
        alipay: String?
    ): BaseResponse<UserInfoResponse> {
        return apiService.modifyUserInfo(
            token,
            user_id,
            ModifyUserInfoModel(real_name, province, city, county, town, default_address, alipay)
        )


    }
}