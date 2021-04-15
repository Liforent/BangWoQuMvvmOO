package com.zues.ruiyu.bangwoqu.module.selfCenter.data

/**
 *@Author liforent
 *@create 2020/9/10 15:48
 */
data class FinancialRecordsResponse(
    var id:Long,
    var create_time:String,
    var update_time:String,
    var type:Int,
    var amount:String,
    var remark:String,
    var user:Long,
    var `package`:String
    ){

}