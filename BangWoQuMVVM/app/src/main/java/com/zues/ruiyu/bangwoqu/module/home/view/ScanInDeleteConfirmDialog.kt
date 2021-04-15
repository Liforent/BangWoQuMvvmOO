package com.zues.ruiyu.bangwoqu.module.home.view

import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleDialogFragment
import com.zues.ruiyu.bangwoqu.module.home.HomeViewModel

class ScanInDeleteConfirmDialog :BaseLifeCycleDialogFragment<HomeViewModel>() {
    override fun getContentLayoutId(): Int = R.layout.df_scan_in_delete_confirm
}