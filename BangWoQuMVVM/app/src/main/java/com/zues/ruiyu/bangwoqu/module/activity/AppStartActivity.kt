package com.zues.ruiyu.bangwoqu.module.activity

import android.content.Intent
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.BaseActivity
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssSPHelper
import com.zues.ruiyu.bangwoqu.module.account.view.LoginActivity

/**
 *@Author liforent
 *@create 2020/9/7 17:37
 */
class AppStartActivity : BaseActivity() {
    lateinit var token: String
    override fun getLayoutId(): Int = R.layout.activity_appstart
    override fun initView() {
        super.initView()
        token = ZssSPHelper.getInstance(this@AppStartActivity)
            .getSharedPreference(Constant.SP_KEY_TOKEN, "") as String
        if (token == "") {
            startActivity(Intent(this@AppStartActivity, LoginActivity::class.java))
        } else {
            startActivity(Intent(this@AppStartActivity, MainActivity::class.java))
        }
        ZLog.e("token:$token")
    }
}