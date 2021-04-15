package com.zues.ruiyu.bangwoqu.module.account.data

/**
 *@Author liforent
 *@create 2020/9/8 14:38
 */
data class AccountResponse(
    var token: String,
    var is_complete: Boolean,
    var username: String,
    var phone: String,
    var nickname: String,
    var real_name: String,
    var id_card_num: String,
    var province: String,
    var city: String,
    var county: String,
    var town: String,
    var default_address: String,
    var customer_service: String,
    var password: Boolean,
    var id: Long
)