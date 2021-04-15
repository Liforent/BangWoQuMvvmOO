package com.zues.ruiyu.bangwoqu.base.mvvm

/**
 *@Author liforent
 *@create 2020/8/24 18:00
 */
enum class StateType {
    SUCCESS,
    ERROR,// 接口请求成功，但是后台返回error  （这部分error msg可能是需要放在自定义的view里的）
    EMPTY,
    LOADING,
    RE_LOGIN,
    SYSTEM_ERROR //404 ,网络等接口请求失败导致的error
}