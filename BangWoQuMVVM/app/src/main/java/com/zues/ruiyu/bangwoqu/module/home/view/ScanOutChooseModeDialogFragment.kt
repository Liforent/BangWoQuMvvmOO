package com.zues.ruiyu.bangwoqu.module.home.view

import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.gone
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.show
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleDialogFragment
import com.zues.ruiyu.bangwoqu.module.home.HomeViewModel
import org.jetbrains.anko.custom.style
import org.jetbrains.anko.support.v4.toast
import java.util.logging.Handler

/**
 *@Author liforent
 *@create 2020/12/4 19:11
 *
 * 第一次进入出库页面的弹窗选择
 */
class ScanOutChooseModeDialogFragment : BaseLifeCycleDialogFragment<HomeViewModel>() {
    var listener: ModeSelectListener? = null

    override fun getContentLayoutId(): Int = R.layout.df_choose_scan_out_mode

    override fun handleViewCreate(view: View) {
        super.handleViewCreate(view)
        val llSelf = view.findViewById<LinearLayout>(R.id.ll_self)
        val llNotSelf = view.findViewById<LinearLayout>(R.id.ll_not_self)
        llSelf.isSelected = true
        view.findViewById<TextView>(R.id.tv_self).typeface =
            Typeface.defaultFromStyle(Typeface.BOLD)
        llSelf.setOnClickListener {
            it.isSelected = true
            llNotSelf.isSelected = false
            view.findViewById<TextView>(R.id.tv_self).typeface =
                Typeface.defaultFromStyle(Typeface.BOLD)
            view.findViewById<ImageView>(R.id.iv_self).show()
            view.findViewById<ImageView>(R.id.iv_not_self).gone()
            listener?.onModeSelected(Constant.IS_SELF)
            view.postDelayed({ dismiss() }, 100)

        }
        llNotSelf.setOnClickListener {
            it.isSelected = true
            llSelf.isSelected = false
            view.findViewById<ImageView>(R.id.iv_self).gone()
            view.findViewById<ImageView>(R.id.iv_not_self).show()
            view.findViewById<TextView>(R.id.tv_not_self).typeface =
                Typeface.defaultFromStyle(Typeface.BOLD)
            listener?.onModeSelected(Constant.IS_NOT_SELF)
            view.postDelayed({ dismiss() }, 100)


        }

        dialog?.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(p0: DialogInterface?, keyCode: Int, p2: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true
                }
                return false
            }
        })

    }

    public fun setModeListener(listener: ModeSelectListener?) {
        this.listener = listener
    }

    interface ModeSelectListener {
        fun onModeSelected(mode: Int)
    }


}