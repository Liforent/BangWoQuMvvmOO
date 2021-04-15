package com.zues.ruiyu.bangwoqu.base.https

/**
 *@Author liforent
 *@create 2020/8/24 14:55
 */
class BasePagingResponse<T>(
    var page_num: Int, var page_size: Int, var total: Int,
    data: T,
    code: Int = -1,
    msg: String = ""
) :
    BaseResponse<T>(data, code, msg) {
}