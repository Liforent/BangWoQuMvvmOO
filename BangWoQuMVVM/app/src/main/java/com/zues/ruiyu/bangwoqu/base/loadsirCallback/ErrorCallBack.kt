package com.zues.ruiyu.bangwoqu.base.loadsirCallback

import com.kingja.loadsir.callback.Callback
import com.zues.ruiyu.bangwoqu.R

/**
 * Created with Android Studio.
 * Description:
 * @author:
 * @date: 2020/02/22
 * Time: 14:29
 */
class ErrorCallBack : Callback() {
    override fun onCreateView(): Int = R.layout.layout_error
}