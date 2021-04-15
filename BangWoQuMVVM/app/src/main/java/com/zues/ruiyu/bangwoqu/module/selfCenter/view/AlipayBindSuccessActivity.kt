package com.zues.ruiyu.bangwoqu.module.selfCenter.view

import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.BaseActivity
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.eventbus.BangwqMessageEvent
import kotlinx.android.synthetic.main.activity_alipa_bind_success.*
import org.greenrobot.eventbus.EventBus

/**
 *@Author liforent
 *@create 2020/9/10 14:26
 */
class AlipayBindSuccessActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_alipa_bind_success
    override fun initView() {
        super.initView()
        tv_submit.setOnClickListener {
            finish()
            EventBus.getDefault().post(BangwqMessageEvent(Constant.EVENT_UPDATE_MINE_ALIPAY))
        }
        title_view.setBack {
            finish()
            EventBus.getDefault().post(BangwqMessageEvent(Constant.EVENT_UPDATE_MINE_ALIPAY))
        }

    }
}