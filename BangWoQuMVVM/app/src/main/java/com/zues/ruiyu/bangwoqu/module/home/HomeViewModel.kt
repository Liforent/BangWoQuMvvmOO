package com.zues.ruiyu.bangwoqu.module.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.zues.ruiyu.bangwoqu.base.https.ApiList
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseViewModel
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.https.BasePagingResponse
import com.zues.ruiyu.bangwoqu.base.https.BaseResponse
import com.zues.ruiyu.bangwoqu.base.https.EmptyResponse
import com.zues.ruiyu.bangwoqu.base.https.initiateRequest
import com.zues.ruiyu.bangwoqu.module.home.data.*
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.UserInfoResponse
import okhttp3.RequestBody

/**
 *@Author liforent
 *@create 2020/9/8 9:54
 */
class HomeViewModel(application: Application) : BaseViewModel<HomeRepository>(application) {
    val mLoadPackagesData: MutableLiveData<BasePagingResponse<List<PackagesListResponse>>> =
        MutableLiveData()
    val mPackageInfoData: MutableLiveData<BaseResponse<PackageInfoResponse>> = MutableLiveData()
    val mCompanyInfoData: MutableLiveData<BasePagingResponse<List<CompanyInfoResponse>>> =
        MutableLiveData()
    val mWarehousingPackageData: MutableLiveData<BaseResponse<WarehousingPackageResponse>> =
        MutableLiveData()
    val mUserInfoData: MutableLiveData<BaseResponse<UserInfoResponse>> = MutableLiveData()

    val mRemovePackageInfoData: MutableLiveData<BaseResponse<EmptyResponse>> = MutableLiveData()

    val mModifyPhoneData: MutableLiveData<BaseResponse<EmptyResponse>> = MutableLiveData()
    val mScanOutData: MutableLiveData<BaseResponse<EmptyResponse>> = MutableLiveData()
    val mQueryPackageStatusBeforeScanOutData: MutableLiveData<BaseResponse<PackagesListResponse>> =
        MutableLiveData()
    val mQueryPackageInfoBeforeScanInData: MutableLiveData<BaseResponse<PackageInfoResponse>> =
        MutableLiveData()
    val mBulkStorageData: MutableLiveData<BaseResponse<EmptyResponse>> = MutableLiveData()
    val mQueryVillageListData: MutableLiveData<BasePagingResponse<List<AreaInfoResponse>>> =
        MutableLiveData()
    val mCompleteReceiverInfoModel: MutableLiveData<BaseResponse<EmptyResponse>> = MutableLiveData()

    fun deletePackage(token: String, id: Int) {
        initiateRequest(
            { mRemovePackageInfoData.value = mRepository.deletePackage(token, id) },
            loadState,
            ApiList.DELETE_PACKAGE
        )

    }

    fun loadPackages(
        token: String,
        page_num: Int?,
        receiver_phone: String?,
        tracking_number: String?,
        shipper: String?,
        courier: String?,
        status: String?
    ) {
        initiateRequest({
            mLoadPackagesData.value = mRepository.loadPackages(
                token,
                page_num,
                receiver_phone,
                tracking_number,
                shipper,
                courier,
                status
            )
        }, loadState, ApiList.LOAD_PACKAGES)

    }

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

    fun searchPackage(
        tracking_number: String,
        com: String?,//	快递公司编码, 如果不填, 则自动识别, 并和返回结果一起返回
        phone: String?    //快递单号收件人手机号, 如果为顺丰快递, 则必填此字段, 详情见快递列表

    ) {
        initiateRequest({
            mPackageInfoData.value = mRepository.queryPackage(
                Constant.TOKEN,
                tracking_number,
                com,
                phone
            )
        }, loadState, ApiList.QUERY_PACKAGE)

    }


    fun getCompanyInfo() {
        initiateRequest({
            mCompanyInfoData.value = mRepository.getCompanyInfo(
                Constant.TOKEN
            )
        }, loadState, ApiList.GET_COMPANY_INFO)

    }

    //扫描出库
    fun scanOut(tracking: String, isSelf: Int) {
        initiateRequest({
            mScanOutData.value = mRepository.scanOut(tracking, isSelf)
        }, loadState, ApiList.SCAN_OUT)

    }

    //出库前查询快递状态
    fun queryPackageStatusBeforeScanOut(tracking: String, isSelf: Int) {
        initiateRequest({
            mQueryPackageStatusBeforeScanOutData.value =
                mRepository.queryPackageStatusBeforeScanOut(tracking, isSelf)
        }, loadState, ApiList.QUERY_PACKAGE_STATUS_BEFORE_SCAN_OUT)

    }

    //入库前查询快递状态
    fun queryPackageStatusBeforeScanIn(tracking: String, phone: String) {
        initiateRequest({
            mQueryPackageInfoBeforeScanInData.value =
                mRepository.queryPackageStatusBeforeScanIn(tracking, phone)
        }, loadState, ApiList.QUERY_PACKAGE_STATUS_BEFORE_SCAN_IN)

    }

    fun warehousingPackage(
        receiver_phone: String,
        tracking_number: String,
        com: String,
        info: List<PackageInfoResponse.PackageInfo>?,
        shipper: Long,
        freight: Float
    ) {
        initiateRequest({
            mWarehousingPackageData.value = mRepository.warehousingPackage(
                Constant.TOKEN,
                WarehousingPackageModel(
                    receiver_phone,
                    tracking_number,
                    com,
                    info,
                    shipper,
                    freight
                )
            )
        }, loadState, ApiList.WAREHOUSING_PACKAGE)

    }


    fun modifyPhone(package_id: Int, receiver_phone: String) {
        initiateRequest({
            mModifyPhoneData.value = mRepository.modifyPhone(package_id, receiver_phone)
        }, loadState, ApiList.MODIFY_PHONE)

    }

    fun bulkStorage(packages: RequestBody) {
        initiateRequest({
            mBulkStorageData.value = mRepository.bulkStorage(packages)
        }, loadState, ApiList.BULK_STORAGE)
    }

    fun queryVillageList(townCode: Long) {
        initiateRequest({
            mQueryVillageListData.value = mRepository.queryVillageList(townCode)
        }, loadState, ApiList.QUERY_VILLAGE_LIST)
    }

    fun completeRecevierInfo(model: CompleteReceiverInfoModel) {
        initiateRequest({
            mCompleteReceiverInfoModel.value = mRepository.completeRecevierInfo(model)
        }, loadState, ApiList.COMPLETE_RECEIVER_INFO)
    }
}