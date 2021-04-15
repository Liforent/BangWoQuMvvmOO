package com.zues.ruiyu.bangwoqu.module.home.data

import java.io.Serializable

/**
 *@Author liforent
 *@create 2020/9/8 13:50
 */
data class PackagesListResponse(
    var id: Int,
    var create_time: String,
    var update_time: String,
    var receiver_phone: String,
    var receiver_address: String?,
    var shipp_phone: String,//快递点电话
    var shipper_address: String,
    var real_name: String?,
    var courier_phone: String?,//快递员电话, 如果此快递还没有人接单, 则没有此字段
    var courier_address: String?,//快递员地址, 如果此快递还没有人接单, 则没有此字段
    var status: Int,
    var tracking_number: String,//快递单号
    var freight: String,//运费
    var remark: String?,//备注
    var com: String?,//快递单号对应的快递公司编码
    var com_name: String?,
    var com_logo: String?,
    var shipper: Int,//寄件人id
    var receiver: String?,//收件人id
    var courier: String?,//快递员id
    var is_self: Int,//0:代取, 1: 自取
    var village: String?,
    var village_code: String,
    var info: List<PackageInfoResponse.PackageInfo>?,
    var default_address: String?

) : Serializable

