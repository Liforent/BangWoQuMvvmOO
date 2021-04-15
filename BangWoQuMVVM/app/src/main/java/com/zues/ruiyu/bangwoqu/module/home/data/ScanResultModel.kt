package com.zues.ruiyu.bangwoqu.module.home.data

/**
 *@Author liforent
 *@create 2020/11/27 14:01
 */
data class ScanResultModel(
    var trackingNumber: String,
    var phoneNumber: String,
    var logo: String,
    var priceInfo: PackageInfoResponse.PriceInfo?
) {
}