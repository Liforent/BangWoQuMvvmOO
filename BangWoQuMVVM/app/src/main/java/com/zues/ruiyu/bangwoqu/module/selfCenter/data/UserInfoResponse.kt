package com.zues.ruiyu.bangwoqu.module.selfCenter.data

import java.io.Serializable

/**
 *@Author liforent
 *@create 2020/9/9 15:31
 */
data class UserInfoResponse(
    var id: Long,
    var openid: String?,
    var last_login: String,
    var is_superuser: Boolean,//是否管理员
    var is_active: Boolean,//是否激活
    var date_joined: String,
    var username: String,
    var phone: String,
    var nickname: String?,//微信昵称, 如果role = 1, 则为快递点名称
    var gender: Int,
    var province: String?,
    var city: String?,
    var county: String?,
    var town: String,
    var avatar: String?,
    var unionid: String?,
    var role: Int,//角色 (1, '代收点'), (2, '快递员'), (3, '收件人')
    var id_card_pic: String?,
    var id_card_num: String?,
    var vehicle_pic: String?,
    var real_name: String?,
    var default_address: String?,
    var order_count: Int,// 订单总量
    var complete_count: Int,// 完成订单量
    var total_amount: String,// 订单总金额
    var complete_amount: String,// 完成订单总金额
    var balance: String,//账户余额
    var alipay: String?,//支付宝
    var password: Boolean


) : Serializable {
}