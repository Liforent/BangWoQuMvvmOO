package com.zues.ruiyu.bangwoqu.module.home.view

import android.view.View
import android.widget.Toast
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseHalfScreenDialog
import kotlinx.android.synthetic.main.df_verify_phone.view.*

/**
 *@Author liforent
 *@create 2020/9/11 10:57
 */
class VerifyPhoneDialog : BaseHalfScreenDialog() {
    var listener: OnConfirmListener? = null

    override fun getContentLayoutId(): Int = R.layout.df_verify_phone

    override fun handleViewCreate(view: View) {
        view.tv_submit.setOnClickListener {
            view.et_name.text.toString()
            if (view.et_name.text.toString().length != 11) {
                Toast.makeText(requireContext(), "手机号码位数不对！", Toast.LENGTH_SHORT).show()
            } else {
                listener?.onConfirm(view.et_name.text.toString())
            }
        }
    }

    interface OnConfirmListener {
        fun onConfirm(phone: String)
    }
}