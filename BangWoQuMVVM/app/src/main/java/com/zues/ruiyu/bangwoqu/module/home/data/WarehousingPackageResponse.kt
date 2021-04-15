package com.zues.ruiyu.bangwoqu.module.home.data

/**
 *@Author liforent
 *@create 2020/9/11 17:50
 */
data class WarehousingPackageResponse(
    var id: Int,
    var info: List<PackageInfoResponse.PackageInfo>,
    var create_time:String,
    var update_time:String,
    var receiver_phone:String,
    var receiver_address:String,
    var real_name:String,
    var status:Int,
    var tracking_number:String,
    var freight:String,
    var shipper:Int,
    var receiver:String,
    var courier:String,
    var shipper_phone:String,
    var shipper_address:String
    ) {
}