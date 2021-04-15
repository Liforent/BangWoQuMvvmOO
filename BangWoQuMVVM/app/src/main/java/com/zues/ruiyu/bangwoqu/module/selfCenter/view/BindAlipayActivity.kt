package com.zues.ruiyu.bangwoqu.module.selfCenter.view

import android.content.Intent
import android.text.TextUtils
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.BaseApplication
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleActivity
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssSPHelper
import com.zues.ruiyu.bangwoqu.module.selfCenter.MineViewModel
import kotlinx.android.synthetic.main.activity_bind_alipay.*
import kotlinx.android.synthetic.main.activity_bind_alipay.title_view
import org.jetbrains.anko.toast

/**
 *@Author liforent
 *@create 2020/9/10 9:59
 */
class BindAlipayActivity : BaseLifeCycleActivity<MineViewModel>() {
    private val userId by lazy {
        ZssSPHelper.getInstance(this).getSharedPreference(Constant.SP_KEY_USER_ID, 0L) as Long
    }

    override fun initData() = false

    override fun initDataObserver() {
        mViewModel.mUserInfoData.observe(this, {
            if (it?.code == Constant.SUCCESS) {
                val mUser = mUserInfo
                mUser?.alipay = et_account.text.toString()
                updateUserInfo(mUserInfo)
                startActivity(
                    Intent(
                        this@BindAlipayActivity,
                        AlipayBindSuccessActivity::class.java
                    )
                )
                finish()
            }

        }
        )
    }

    override fun getLayoutId(): Int = R.layout.activity_bind_alipay
    override fun initView() {
        super.initView()
        if (!TextUtils.isEmpty((application as BaseApplication).userInfo?.alipay)) {
            title_view.setTitleText("修改支付宝账号")
            tv_submit.text = "修改并提交"
        } else {
            title_view.setTitleText("添加支付宝账号")
            tv_submit.text = "确认并提交"
        }
        tv_submit.setOnClickListener {
            if (et_account.text.toString() == "") {
                toast("支付宝账号不能为空！")
                return@setOnClickListener
            }
            if (et_name.text.toString() == "") {
                toast("姓名不能为空！")
                return@setOnClickListener
            }
            if (et_id_number.text.toString() == "") {
                toast("身份证号码不能为空！")
                return@setOnClickListener
            }
            if (et_phone.text.toString() == "") {
                toast("手机号码不能为空！")
                return@setOnClickListener
            }
            showLoadingView()
            mViewModel.modifyUserInfo(
                Constant.TOKEN, userId.toLong(), null, null, null, null, null, null,
                et_account.text.toString()
            )
        }
        title_view.setBack {
            finish()
        }

    }

}