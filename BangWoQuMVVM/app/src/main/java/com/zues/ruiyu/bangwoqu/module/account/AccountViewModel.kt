package com.zues.ruiyu.bangwoqu.module.account

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.https.ApiList
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseViewModel
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.https.BaseResponse
import com.zues.ruiyu.bangwoqu.base.https.EmptyResponse
import com.zues.ruiyu.bangwoqu.base.https.initiateRequest
import com.zues.ruiyu.bangwoqu.module.account.data.AccountResponse
import com.zues.ruiyu.bangwoqu.module.account.data.LoginModel
import com.zues.ruiyu.bangwoqu.module.account.data.RegisterModel
import com.zues.ruiyu.bangwoqu.module.account.data.VerificationCodeModel
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.UserInfoResponse

/**
 *@Author liforent
 *@create 2020/9/8 15:08
 */
class AccountViewModel(application: Application) : BaseViewModel<AccountRepository>(application) {
    val mUserInfoData: MutableLiveData<BaseResponse<UserInfoResponse>> = MutableLiveData()

    val mAccountData: MutableLiveData<BaseResponse<AccountResponse>> = MutableLiveData()
    val mVerificationCodeData: MutableLiveData<BaseResponse<EmptyResponse>> = MutableLiveData()
    fun login(phone: String, code: String) {
        if (phone.isNullOrEmpty() || code.isNullOrEmpty()) {
            Toast.makeText(getApplication(), R.string.accountOrpasswordempty, Toast.LENGTH_SHORT)
                .show()
        } else {
            initiateRequest({
                mAccountData.value =
                    mRepository.login(LoginModel(Constant.AUTH_TYPE_LOGIN, phone, code))

            }, loadState, ApiList.LOGIN)
        }
    }

     fun getVerificationCode(phone: String, type: String) {
        initiateRequest({
            mVerificationCodeData.value = mRepository.getVerificationCode(
                VerificationCodeModel(type, phone)
            )
        }, loadState, ApiList.GET_VERIFICATION_CODE)


    }

    fun updateUserInfo(
        token: String,
        user_id: Long,
        real_name: String?,
        province: String?,
        city: String?,
        county: String?,
        town: String?,
        default_address: String?,
        alipay: String?

    ) {
        initiateRequest({
            mUserInfoData.value = mRepository.updateUserInfo(
                token,
                user_id,
                real_name,
                province,
                city,
                county,
                town,
                default_address,
                alipay
            )
        }, loadState, ApiList.MODIFY_USER_INFO)

    }

    fun register(registerModel: RegisterModel) {
        initiateRequest({
            mAccountData.value = mRepository.register(
                registerModel
            )

        }, loadState, ApiList.REGISTER)

    }
}