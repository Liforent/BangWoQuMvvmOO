package com.zues.ruiyu.bangwoqu.module.home.view

import android.view.View
import android.widget.Button
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.eventbus.BangwqMessageEvent
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleDialogFragment
import com.zues.ruiyu.bangwoqu.module.home.HomeViewModel
import kotlinx.android.synthetic.main.df_scan_in_quit.*
import org.greenrobot.eventbus.EventBus

/**
 *@Author liforent
 *@create 2020/12/16 16:53
 */
class ScanInQuitDialogFragment : BaseLifeCycleDialogFragment<HomeViewModel>() {
    override fun getContentLayoutId(): Int = R.layout.df_scan_in_quit
    override fun initEvent(view: View) {
        super.initEvent(view)
        btn_cancel.setOnClickListener {
            dismiss()
        }
        btn_confirm.setOnClickListener {
            EventBus.getDefault().post(BangwqMessageEvent(Constant.EVENT_QUIT))
        }
    }
}