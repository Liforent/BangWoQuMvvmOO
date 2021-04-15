package com.zues.ruiyu.bangwoqu.module.home.data

import java.io.Serializable

/**
 *@Author liforent
 *@create 2020/9/11 13:51
 */
data class CompanyInfoResponse(
    var id: Int,
    var code: String,//快递公司编码
    var name: String,
    var category: String,//快递公司分类
    var logo: String
) : Serializable {
}