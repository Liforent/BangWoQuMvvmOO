package com.zues.ruiyu.bangwoqu.module.selfCenter

import androidx.lifecycle.MutableLiveData
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.https.BasePagingResponse
import com.zues.ruiyu.bangwoqu.base.https.BaseResponse
import com.zues.ruiyu.bangwoqu.base.https.EmptyResponse
import com.zues.ruiyu.bangwoqu.base.mvvm.ApiRepository
import com.zues.ruiyu.bangwoqu.base.mvvm.State
import com.zues.ruiyu.bangwoqu.module.account.data.ModifyUserInfoModel
import com.zues.ruiyu.bangwoqu.module.account.data.WithdrawModel
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.FinancialRecordsResponse
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.UserInfoResponse

/**
 *@Author liforent
 *@create 2020/9/8 10:08
 */
class MineRepository(var loadState: MutableLiveData<State>) : ApiRepository() {

    suspend fun getUserInfo(
        token: String,
        id: Long
    ): BaseResponse<UserInfoResponse> {
        return apiService.getUserInfo(token, id)
    }

    suspend fun withdraw(
        money: Double,
        withdrawal_method: String

    ): BaseResponse<EmptyResponse> {
        return apiService.withdraw(Constant.TOKEN, WithdrawModel(money, withdrawal_method))
    }

    suspend fun modifyUserInfo(
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

    suspend fun getFinancialRecords(
        token: String,
        mPage: Int,
        user_id: Long,
        type: Int?
    ): BasePagingResponse<List<FinancialRecordsResponse>> {
        return apiService.getFinancialRecords(token, mPage, user_id, type)
    }

}