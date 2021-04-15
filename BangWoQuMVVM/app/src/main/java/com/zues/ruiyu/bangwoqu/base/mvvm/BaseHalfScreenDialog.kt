package com.zues.ruiyu.bangwoqu.base.mvvm

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatDialogFragment
import com.zues.ruiyu.bangwoqu.R

abstract class BaseHalfScreenDialog : AppCompatDialogFragment() {

    abstract fun getContentLayoutId(): Int
    abstract fun handleViewCreate(view: View)
    open var mAlpha = 64f


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(0x00000000))
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

    }


    open fun setAlpha() {
        mAlpha = 64f
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getContentLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //坑，注意顺序否则会导致tv_cancel在子类中重新定义的点击事件被覆盖=，=
        setButtonDismiss(view)
        handleViewCreate(view)
        initEvent(view)
    }

    private fun setButtonDismiss(view: View) {
        view.findViewById<View>(R.id.tv_cancel)?.setOnClickListener { dismiss() }
        view.findViewById<View>(R.id.ll_root)?.setOnClickListener {
            dismiss()
        }
        view.findViewById<View>(R.id.cl_root)?.setOnClickListener {
        }

    }

    open fun initEvent(view: View) {}


}