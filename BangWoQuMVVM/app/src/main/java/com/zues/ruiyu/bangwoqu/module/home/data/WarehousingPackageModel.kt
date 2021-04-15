package com.zues.ruiyu.bangwoqu.module.home.data

/**
 *@Author liforent
 *@create 2020/9/11 17:53
 */
data class WarehousingPackageModel(
    var receiver_phone:String,
    var tracking_number:String,
    var com:String,
    var info: List<PackageInfoResponse.PackageInfo>?,
    var shipper: Long,
    var freight:Float//运费
)