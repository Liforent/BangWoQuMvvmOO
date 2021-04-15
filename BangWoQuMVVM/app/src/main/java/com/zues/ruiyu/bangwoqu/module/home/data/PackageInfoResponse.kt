package com.zues.ruiyu.bangwoqu.module.home.data

import java.io.Serializable

/**
 *@Author liforent
 *@create 2020/12/2 19:51
 */
data class PackageInfoResponse(
    var tracking_number: String,
    var phone: String,
    var com: String,
    var com_name: String?,
    var com_logo: String?,
    var info: List<PackageInfo>?,
    var price_info: PriceInfo?,
    var town_code:Long?
) : Serializable {
    data class PackageInfo(
        var time: String,
        var context: String,
        var ftime: String,
        var areaCode: String?,
        var areaName: String?,
        var status: String,
        var town_code: String?
    ) : Serializable

    data class PriceInfo(
        var id: Int,
        var price: String?,
        var name: String?,
        var shipper: Int,
        var area: Long?
    ) : Serializable {}
}



