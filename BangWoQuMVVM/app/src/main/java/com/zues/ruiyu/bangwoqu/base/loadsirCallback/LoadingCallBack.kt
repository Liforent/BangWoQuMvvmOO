package com.zues.ruiyu.bangwoqu.base.loadsirCallback

import com.kingja.loadsir.callback.Callback
import com.zues.ruiyu.bangwoqu.R

class LoadingCallBack : Callback() {
    override fun onCreateView(): Int = R.layout.layout_loading
}