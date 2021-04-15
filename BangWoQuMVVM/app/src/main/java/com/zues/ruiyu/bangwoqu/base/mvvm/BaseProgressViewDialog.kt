package com.zues.ruiyu.bangwoqu.base.mvvm

import android.view.View
import com.zues.ruiyu.bangwoqu.R
import kotlinx.android.synthetic.main.df_progress_view.view.*

/**
 *@Author liforent
 *@create 2020/11/13 18:19
 */
class BaseProgressViewDialog(var msg: String) : BaseHalfScreenDialog() {
    override fun getContentLayoutId(): Int = R.layout.df_progress_view

    override fun handleViewCreate(view: View) {
        view.tv_msg.text = msg
    }
}