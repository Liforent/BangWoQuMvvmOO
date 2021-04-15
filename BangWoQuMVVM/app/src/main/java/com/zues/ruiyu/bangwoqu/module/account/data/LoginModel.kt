package com.zues.ruiyu.bangwoqu.module.account.data

import com.zues.ruiyu.bangwoqu.base.Constant

/**
 *@Author liforent
 *@create 2020/9/8 15:39
 */
data class LoginModel(
    val type: String = Constant.AUTH_TYPE_LOGIN,
    var phone: String,
    var code: String
)