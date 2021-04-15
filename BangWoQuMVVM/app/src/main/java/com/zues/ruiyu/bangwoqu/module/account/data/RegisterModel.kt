package com.zues.ruiyu.bangwoqu.module.account.data

/**
 *@Author liforent
 *@create 2020/9/8 16:49
 */
data class RegisterModel(
    var type: String,
    var code: String,
    var nickname: String,
    var phone: String,
    var real_name:String,
    var id_card_num:String,
    var province:String,
    var city:String,
    var county:String,
    var town:String,
    var default_address:String,
    var role:Int
) {
}