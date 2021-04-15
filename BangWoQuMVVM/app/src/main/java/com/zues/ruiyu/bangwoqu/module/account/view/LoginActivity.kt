package com.zues.ruiyu.bangwoqu.module.account.view

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.Observer
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.BaseApplication
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleActivity
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.Constant.SP_KEY_USER_ID
import com.zues.ruiyu.bangwoqu.base.https.onResponse
import com.zues.ruiyu.bangwoqu.base.commonUtils.AppManager
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssSPHelper
import com.zues.ruiyu.bangwoqu.module.account.AccountViewModel
import com.zues.ruiyu.bangwoqu.module.activity.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit

/**
 *@Author liforent
 *@create 2020/9/8 14:37
 */

//
class LoginActivity : BaseLifeCycleActivity<AccountViewModel>() {
    private var mExitTime: Long = 0
    override fun getLayoutId(): Int = R.layout.activity_login
    override fun initData(): Boolean = false


    override fun initView() {
        super.initView()
        tv_get_code
            .setNormalText("获取验证码")
            .setCountDownText("", "后重发")
            .setCloseKeepCountDown(true) //关闭页面保持倒计时开关
            .setCountDownClickable(false) //倒计时期间点击事件是否生效开关
            .setShowFormatTime(true) //是否格式化时间
            .setIntervalUnit(TimeUnit.SECONDS)
            .setOnCountDownStartListener {}
            .setOnCountDownTickListener {}
            .setOnCountDownFinishListener { tv_get_code!!.text = "获取验证码" }
    }

    override fun initEvent() {
        tv_get_code.setOnClickListener {
            getVerificationCode()
            et_code.requestFocus()
        }
        tv_login.setOnClickListener {
            login()
        }
        ll_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }


    private fun getVerificationCode() {
        if (et_name.text.isNullOrEmpty() || et_name.text.length != 11) {
            Toast.makeText(this, R.string.wrong_phone_number, Toast.LENGTH_SHORT)
                .show()
        } else {
            showLoadingView()
            mViewModel.getVerificationCode(et_name.text.toString(), Constant.AUTH_TYPE_LOGIN)
        }

    }

    private fun login() {
        showLoadingView()
        mViewModel.login(et_name.text.toString(), et_code.text.toString())
    }

    override fun initDataObserver() {

        //账户信息跟用户信息返回数据不同，并且无法控制用户登录，对于账户信息的部分数据需要持久化保存和更新
        mViewModel.mAccountData.observe(this, Observer {
            it.onResponse(mViewModel.loadState) {
                toast("登录成功！")
                (application as BaseApplication).updateAccountInfo(it.data)
                ZssSPHelper.getInstance(this@LoginActivity)
                    .put(Constant.SP_KEY_TOKEN, "JWT " + it.data.token)
                ZssSPHelper.getInstance(this@LoginActivity).put(SP_KEY_USER_ID, it.data.id)
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }

        })
        mViewModel.mVerificationCodeData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                tv_get_code.startCountDown(59)
            }
        })
    }

    override fun onBackPressed() {
        val time = System.currentTimeMillis()
        if (time - mExitTime > 2000) {
            toast("再次点击退出APP")
            mExitTime = time
        } else {
            AppManager.instance.exitApp(this)
        }
    }

}