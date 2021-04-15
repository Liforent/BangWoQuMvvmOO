package com.zues.ruiyu.bangwoqu.module.home.view

import android.content.DialogInterface
import android.os.Bundle
import android.util.EventLog
import android.view.KeyEvent
import android.view.View
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.base.eventbus.BangwqMessageEvent
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseHalfScreenDialog
import kotlinx.android.synthetic.main.scan_out_dialog.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.toast

/**
 *@Author liforent
 *@create 2020/11/10 11:57
 */
class ScanOutDialog : BaseHalfScreenDialog() {
    var status: Int? = null
    override fun getContentLayoutId() = R.layout.scan_out_dialog

    override fun handleViewCreate(view: View) {
        status = arguments?.getInt(Constant.DATA_SCAN_OUT_MODE)
        when (status) {
            Constant.IS_SELF -> {
                view.tv_title.text = "用户自取出库？"
            }
            Constant.IS_NOT_SELF -> {
                view.tv_title.text = "配送员代取出库？"
            }

        }
        view.tv_submit_default.setOnClickListener {
            EventBus.getDefault().post(BangwqMessageEvent(Constant.EVENT_SUBMIT))
            dismiss()
        }
        view.tv_cancel.setOnClickListener {
            EventBus.getDefault().post(BangwqMessageEvent(Constant.EVENT_CANCEL))
            dismiss()
        }
    }
}