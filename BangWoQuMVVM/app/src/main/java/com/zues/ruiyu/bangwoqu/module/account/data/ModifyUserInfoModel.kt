package com.zues.ruiyu.bangwoqu.module.account.data

/**
 *@Author liforent
 *@create 2020/9/10 13:44
 */
data class ModifyUserInfoModel(
    var real_name: String?,
    var province: String?,
    var city: String?,
    var county: String?,
    var town: String?,
    var default_address: String?,
    var alipay: String?
    ) {
}