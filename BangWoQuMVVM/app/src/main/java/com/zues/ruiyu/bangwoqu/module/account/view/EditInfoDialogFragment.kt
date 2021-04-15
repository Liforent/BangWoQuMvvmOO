package com.zues.ruiyu.bangwoqu.module.account.view

import android.text.InputFilter
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseHalfScreenDialog
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.commonUtils.InputMethodUtils
import kotlinx.android.synthetic.main.half_df_edit_info.*


/**
 *@Author liforent
 *@create 2020/9/16 22:08
 */
class EditInfoDialogFragment(private var clickListener: DFOnClickListener?) :
    BaseHalfScreenDialog() {
    private val strTitle by lazy {
        arguments?.getString(Constant.DATA_EDIT_TITLE)
    }
    private val strHint by lazy {
        arguments?.getString(Constant.DATA_EDIT_HINT)
    }


    override fun getContentLayoutId(): Int = R.layout.half_df_edit_info

    override fun handleViewCreate(view: View) {
        view.findViewById<EditText>(R.id.et_content).hint = strHint
        view.findViewById<TextView>(R.id.tv_title).text = strTitle
        if ("修改昵称" == strTitle) {
            view.findViewById<EditText>(R.id.et_content).apply {
                filters = arrayOf<InputFilter>(
                    InputFilter.LengthFilter(
                        8
                    )
                )
            }
        }
        view.findViewById<TextView>(R.id.tv_left_btn).setOnClickListener {
            if (clickListener != null) {
                clickListener?.onLeftBtnClicked()
                InputMethodUtils.closeKeyBoard(requireActivity(),et_content)
                dismiss()
            }
        }
        view.findViewById<TextView>(R.id.tv_right_btn).setOnClickListener {
            if (clickListener != null) {
                clickListener?.onRightBtnClicked(view.findViewById<EditText>(R.id.et_content).text.toString())
                InputMethodUtils.closeKeyBoard(requireActivity(),et_content)
                dismiss()
            }
        }


    }

    fun setOnclick(clickListener: DFOnClickListener?) {
        this.clickListener = clickListener
    }

    interface DFOnClickListener {
        fun onLeftBtnClicked()
        fun onRightBtnClicked(etContent: String?)
    }



}