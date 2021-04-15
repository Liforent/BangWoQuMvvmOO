package com.zues.ruiyu.bangwoqu.module.account.view

import android.content.Intent
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleActivity
import com.zues.ruiyu.bangwoqu.module.account.AccountViewModel
import com.zues.ruiyu.bangwoqu.module.activity.MainActivity
import kotlinx.android.synthetic.main.activity_register_success.*

/**
 *@Author liforent
 *@create 2020/9/8 21:29
 */
class RegisterSuccessActivity : BaseLifeCycleActivity<AccountViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_register_success
    override fun initData() = false
    override fun initView() {
        super.initView()
        tv_login.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun initDataObserver() {
    }
}