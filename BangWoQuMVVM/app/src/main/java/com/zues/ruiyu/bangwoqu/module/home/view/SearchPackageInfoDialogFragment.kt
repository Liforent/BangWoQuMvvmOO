package com.zues.ruiyu.bangwoqu.module.home.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialog
import com.gyf.immersionbar.ImmersionBar
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.commonUtils.ClassUtil
import com.zues.ruiyu.bangwoqu.base.commonUtils.InputMethodUtils
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssDeviceHelper
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleDialogFragment
import com.zues.ruiyu.bangwoqu.module.home.HomeViewModel
import kotlinx.android.synthetic.main.df_search_package_info.*

/**
 *@Author liforent
 *@create 2020/12/14 11:25
 *
 * 首页搜索快递
 */
class SearchPackageInfoDialogFragment : BaseLifeCycleDialogFragment<HomeViewModel>() {
    override fun getContentLayoutId(): Int = R.layout.df_search_package_info

    override fun handleViewCreate(view: View) {
        super.handleViewCreate(view)
        ImmersionBar.with(this)
//            .transparentBar()
            .statusBarColor(R.color.white)
            .autoStatusBarDarkModeEnable(true,0.2f)
            .init()
        InputMethodUtils.openKeyBoard(requireActivity(), et_search)
    }

    override fun initEvent(view: View) {
        super.initEvent(view)
        tv_cancel.setOnClickListener {
            dismiss()
        }
    }
}