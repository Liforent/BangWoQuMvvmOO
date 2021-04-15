package com.zues.ruiyu.bangwoqu.base.https

/**
 *@Author liforent
 *@create 2020/8/24 14:55
 */
open class BaseResponse<T>(var data: T, var code: Int = -1, var msg: String = "") {
}