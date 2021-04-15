package com.zues.ruiyu.bangwoqu.base.mvvm

import androidx.annotation.StringRes

/**
 *@Author liforent
 *@create 2020/8/24 18:02
 */
data class State(
    var code : StateType,
    var msg:String = "",
    @StringRes var tip:Int = 0
)