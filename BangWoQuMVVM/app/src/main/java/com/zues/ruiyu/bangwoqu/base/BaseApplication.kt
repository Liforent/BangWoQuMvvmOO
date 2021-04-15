package com.zues.ruiyu.bangwoqu.base

import android.app.Application
import android.content.Context
import com.kingja.loadsir.core.LoadSir
import com.squareup.leakcanary.LeakCanary
import com.tencent.mmkv.MMKV
import com.zues.ruiyu.bangwoqu.base.loadsirCallback.EmptyCallBack
import com.zues.ruiyu.bangwoqu.base.loadsirCallback.ErrorCallBack
import com.zues.ruiyu.bangwoqu.base.loadsirCallback.LoadingCallBack
import com.zues.ruiyu.bangwoqu.module.account.data.AccountResponse
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.UserInfoResponse
import org.litepal.LitePal

/**
 * Created with Android Studio.
 * Description:
 * @author: Wangjianxian
 * @date: 2020/02/22
 * Time: 14:27
 */
open class BaseApplication : Application() {


    companion object {
        lateinit var sAppContext: Context

    }

    var accountInfo: AccountResponse? = null
    var userInfo: UserInfoResponse? = null
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        LoadSir.beginBuilder()
            .addCallback(ErrorCallBack())
            .addCallback(LoadingCallBack())
            .addCallback(EmptyCallBack())
            .commit()
        sAppContext = this
        if(LeakCanary.isInAnalyzerProcess(this)){
            return
        }
        LeakCanary.install(this)
        LitePal.initialize(this)
    }

    fun updateAccountInfo(accountResponse: AccountResponse) {
        accountInfo = accountResponse
    }

    private val getAccountInfo = accountInfo

    fun updateUserInfo(userInfoResponse: UserInfoResponse?) {
        userInfo = userInfoResponse
    }

    fun getCurUserInfo(): UserInfoResponse? {
        return userInfo
    }
}