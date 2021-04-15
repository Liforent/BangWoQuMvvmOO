package com.zues.ruiyu.bangwoqu.base

/**
 *@Author liforent
 *@create 2020/8/24 15:01
 */
object Constant {

    const val isRelease = true
    const val forceLog = true
    const val isDebug = true

    const val FIRST_PAGE = 1 //分页的第一页。

    const val TAB_HOME = 0
    const val TAB_MINE = 1

    const val TIPS_TYPE_SUCCESS = 500
    const val TIPS_TYPE_WARN = 501
    const val TIPS_TYPE_ERROR = 502


    private const val RELEASE_DOMAIN = "https://bwqwxapi.mobiw.com"
    private const val DEBUG_DOMAIN = "http://47.106.217.160:8297"


    const val SUCCESS = 1
    const val NOT_LOGIN = -1001

    var TOKEN = ""
    var DOMAIN = if (isRelease) {
        RELEASE_DOMAIN
    } else {
        DEBUG_DOMAIN
    }

    //包裹是否自取
    const val IS_SELF = 1 //自取
    const val IS_NOT_SELF = 0 //代取



    //包裹状态
    const val PACKAGE_STATUS_DONE = 5//完成
    const val PACKAGE_STATUS_DELIVERING = 4//配送
    const val PACKAGE_STATUS_GET_PACKAGE = 3//取件
    const val PACKAGE_STATUS_GET_ORDER = 2//接单
    const val PACKAGE_STATUS_ATTEMPT_REPLACE = 1//代取申请
    const val PACKAGE_STATUS_WAREHOUSE = 0//入库


    //包裹状态 12.14
    const val PACKAGE_DETAIL_STATUS_SIGNED = 5//已签收
    const val PACKAGE_DETAIL_STATUS_DELIVERED = 4//已送达
    const val PACKAGE_DETAIL_STATUS_DELIVERING = 3//派送中
    const val PACKAGE_DETAIL_STATUS_PICK_UP_ORDER = 2//取件中
    const val PACKAGE_DETAIL_STATUS_WAIT_FOR_COURIER = 1//待抢单
    const val PACKAGE_DETAIL_STATUS_WAREHOUSE = 0//入库



    //SP key
    const val SP_KEY_TOKEN = "key_token"
    const val SP_KEY_USER_ID = "key_user_id"
    const val SP_KEY_FEE = "key_fee_v_0_1"


    //messageEvent
    const val EVENT_UPDATE_HOME_SET_PSW = "event_update_home_set_psw"
    const val EVENT_UPDATE_HOME = "event_update_home"
    const val EVENT_UPDATE_MINE_ALIPAY = "event_update_mine_alipay"
    const val EVENT_UPDATE_MINE_USERINFO = "event_update_mine_userinfo"
    const val EVENT_SUBMIT = "event_submit"
    const val EVENT_CANCEL = "event_cancel"
    const val EVENT_COMELETE_ADDRESS="event_comelete_address"
    const val EVENT_QUIT = "event_quit"


    const val DATA_BALANCE = "data_balance"
    const val DATA_ALIPAY = "data_alipay"
    const val DATA_USER_ID = "data_user_id"
    const val DATA_USER_PHONE = "data_user_phone"
    const val DATA_PHONE = "data_phone"
    const val DATA_TOWN_CODE = "data_town_code"
    const val DATA_PACKAGE_INFO = "data_package_info"
    const val DATA_USER_INFO = "data_user_info"
    const val DATA_EDIT_TITLE = "data_edit_title"
    const val DATA_EDIT_HINT = "data_edit_hint"
    const val DATA_PACKAGE_ID = "data_package_id"
    const val DATA_TRACKING_NUMBER = "data_tracking_number"
    const val DATA_FEE = "data_fee"
    const val DATA_SCAN_RESULT = "data_scan_result"
    const val DATA_SCAN_OUT_MODE = "data_scan_out_mode"
    const val DATA_POSITION = "data_position"
    const val DATA_ITEM = "data_item"


    const val WITHDRAW_TYPE_ALIPAY = "alipay"

    const val AUTH_TYPE_LOGIN = "login"
    const val AUTH_TYPE_REGISTER = "register"


    const val ROLE_TYPE_DEFAULT = 1

}