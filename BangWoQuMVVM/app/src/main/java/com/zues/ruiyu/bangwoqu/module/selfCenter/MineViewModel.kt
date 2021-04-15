package com.zues.ruiyu.bangwoqu.module.selfCenter

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.zues.ruiyu.bangwoqu.base.https.ApiList
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseViewModel
import com.zues.ruiyu.bangwoqu.base.https.BasePagingResponse
import com.zues.ruiyu.bangwoqu.base.https.BaseResponse
import com.zues.ruiyu.bangwoqu.base.https.EmptyResponse
import com.zues.ruiyu.bangwoqu.base.https.initiateRequest
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.FinancialRecordsResponse
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.UserInfoResponse

/**
 *@Author liforent
 *@create 2020/9/8 10:09
 */
class MineViewModel(application: Application) : BaseViewModel<MineRepository>(application) {
    val mUserInfoData: MutableLiveData<BaseResponse<UserInfoResponse>> = MutableLiveData()
    val mWithdrawData: MutableLiveData<BaseResponse<EmptyResponse>> = MutableLiveData()
    val mFinancialRecordsData: MutableLiveData<BasePagingResponse<List<FinancialRecordsResponse>>> =
        MutableLiveData()


    fun getUserInfo(
        token: String,
        id: Long
    ) {
        initiateRequest(
            {
                mUserInfoData.value = mRepository.getUserInfo(token, id)
            }, loadState, ApiList.GET_USER_INFO
        )

    }


    fun withDraw(
        money: Double,
        withdrawal_method: String
    ) {
        initiateRequest({
            mWithdrawData.value = mRepository.withdraw(money, withdrawal_method)
        },loadState, ApiList.WITHDRAW)

    }

    fun modifyUserInfo(
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
            mUserInfoData.value =   mRepository.modifyUserInfo(
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
        },loadState, ApiList.MODIFY_USER_INFO)

    }


    fun getFinancialRecords(
        token: String,
        mPage: Int,
        user_id: Long,
        type: Int?
    ) {
        initiateRequest({
          mFinancialRecordsData.value =   mRepository.getFinancialRecords(token, mPage, user_id, type)
        },loadState, ApiList.GET_FINANCIAL_RECORDS)

    }

}