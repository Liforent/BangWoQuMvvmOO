package com.zues.ruiyu.bangwoqu.module.setting.view

import android.view.View
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleActivity
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.base.eventbus.BangwqMessageEvent
import com.zues.ruiyu.bangwoqu.base.https.onResponse
import com.zues.ruiyu.bangwoqu.module.setting.data.SettingPswModel
import com.zues.ruiyu.bangwoqu.module.setting.SettingPswViewModel
import kotlinx.android.synthetic.main.activity_setting_psw.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast

/**
 *@Author liforent
 *@create 2020/9/8 23:26
 */
class SettingPswActivity : BaseLifeCycleActivity<SettingPswViewModel>() {
    override fun initData() = false

    override fun initDataObserver() {
        mViewModel.mSettingPswData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                ZLog.e(mUserInfo?.username.toString())
                val mUser = mUserInfo
                mUser?.password = true
//                mUser?.username = "123"
                updateUserInfo(mUser)
                ZLog.e(mUser?.password.toString())
                toast("设置成功！")
                finish()
                EventBus.getDefault().post(BangwqMessageEvent(Constant.EVENT_UPDATE_HOME_SET_PSW))
            }
        })
    }

    override fun getLayoutId(): Int = R.layout.activity_setting_psw
    override fun initView() {
        super.initView()
        if (mUserInfo?.password!!) {
            title_view.setTitleText("修改密码")
            tv_submit.text = "立即修改"
            tv0.visibility = View.GONE
            et_name.visibility = View.GONE

        } else {
            title_view.setTitleText("设置管理后台密码")
            tv_submit.text = "立即保存"
            tv0.visibility = View.VISIBLE
            et_name.visibility = View.VISIBLE
            et_name.text = mUserInfo?.phone
        }
        tv_submit.setOnClickListener {
            showLoadingView()
            mViewModel.setPsw(
                Constant.TOKEN,
                SettingPswModel(et_psw.text.toString(), et_psw2.text.toString())
            )
        }
        title_view.setBack {
            finish()
        }


    }


}