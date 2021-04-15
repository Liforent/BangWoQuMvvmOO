package com.zues.ruiyu.bangwoqu.module.home

import androidx.lifecycle.MutableLiveData
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.https.BasePagingResponse
import com.zues.ruiyu.bangwoqu.base.https.BaseResponse
import com.zues.ruiyu.bangwoqu.base.https.EmptyResponse
import com.zues.ruiyu.bangwoqu.base.mvvm.ApiRepository
import com.zues.ruiyu.bangwoqu.base.mvvm.State
import com.zues.ruiyu.bangwoqu.module.home.data.*
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.UserInfoResponse
import okhttp3.RequestBody

/**
 *@Author liforent
 *@create 2020/9/8 9:55
 */

class HomeRepository(var loadState: MutableLiveData<State>) :
    ApiRepository() {
    suspend fun loadPackages(
        token: String,
        page_num: Int?,
        receiver_phone: String?,
        tracking_number: String?,
        shipper: String?,
        courier: String?,
        status: String?
    ): BasePagingResponse<List<PackagesListResponse>> {
        return apiService.loadPackages(
            token,
            page_num,
            receiver_phone,
            tracking_number,
            shipper,
            courier,
            status
        )
    }

    suspend fun queryPackage(
        token: String,
        tracking_number: String,
        com: String?,
        phone: String?
    ): BaseResponse<PackageInfoResponse> {
        return apiService.queryPackage(token, tracking_number, com, phone)
    }

    suspend fun scanOut(
        tracking_number: String,
        isSelf: Int
    ): BaseResponse<EmptyResponse> {
        return apiService.scanOut(Constant.TOKEN, ScanOutModel(tracking_number, isSelf))
    }

    suspend fun queryPackageStatusBeforeScanOut(
        tracking_number: String,
        isSelf: Int
    ): BaseResponse<PackagesListResponse> {
        return apiService.queryPackageStatusBeforeScanOut(
            Constant.TOKEN,
            ScanOutModel(tracking_number, isSelf)
        )

    }

    suspend fun queryPackageStatusBeforeScanIn(
        tracking_number: String,
        phone: String
    ): BaseResponse<PackageInfoResponse> {
       // var a = "JWT eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoxMDc5LCJ1c2VybmFtZSI6IjE1MTk4NzkyNDk3IiwiZXhwIjoyNDcyMDI4OTc0LCJlbWFpbCI6IiIsIm9wZW5pZCI6bnVsbCwicGhvbmUiOiIxNTE5ODc5MjQ5NyIsInJvbGUiOjEsImlzX2NvbXBsZXRlIjpmYWxzZX0.pPB_K9JaW_7Mx2xOIswCoGUpzYkqleWnwIJhNYsnJbE"

        return apiService.queryPackageStatusBeforeScanIn(Constant.TOKEN, tracking_number, phone)

    }

    suspend fun getCompanyInfo(
        token: String
    ): BasePagingResponse<List<CompanyInfoResponse>> {
        return apiService.getCompanyInfo(token)
    }

    suspend fun getUserInfo(
        token: String,
        id: Long
    ): BaseResponse<UserInfoResponse> {
        return apiService.getUserInfo(token, id)
    }

    suspend fun warehousingPackage(
        token: String,
        model: WarehousingPackageModel
    ): BaseResponse<WarehousingPackageResponse> {
        return apiService.warehousingPackage(token, model)
    }

    suspend fun deletePackage(
        token: String,
        id: Int
    ): BaseResponse<EmptyResponse> {
        return apiService.deletePackage(token, id)
    }

    suspend fun modifyPhone(
        package_id: Int,
        receiver_phone: String
    ): BaseResponse<EmptyResponse> {
        return apiService.modifyPhone(Constant.TOKEN, package_id, ModifyPhoneModel(receiver_phone))
    }

    suspend fun bulkStorage(
        packages: RequestBody
    ): BaseResponse<EmptyResponse> {
        return apiService.bulkStorage(Constant.TOKEN, packages)
    }

    suspend fun queryVillageList(
        townCode: Long
    ): BasePagingResponse<List<AreaInfoResponse>> {
        return apiService.queryVillageList(Constant.TOKEN, townCode, 100, 1)
    }

    suspend fun completeRecevierInfo(
        model: CompleteReceiverInfoModel
    ): BaseResponse<EmptyResponse> {
        return apiService.completeReceiverInfo(Constant.TOKEN, model)
    }
}