package com.zues.ruiyu.bangwoqu.base.mvvm

import ApiService
import com.zues.ruiyu.bangwoqu.base.https.RetrofitFactory

/**
 *@Author liforent
 *@create 2020/8/24 18:22
 */
open class ApiRepository() : BaseRepository() {

    val apiService: ApiService by lazy {

        RetrofitFactory.instance.create(ApiService::class.java)
    }

}