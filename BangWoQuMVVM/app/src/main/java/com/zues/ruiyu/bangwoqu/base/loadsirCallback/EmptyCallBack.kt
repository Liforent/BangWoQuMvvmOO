package com.zues.ruiyu.bangwoqu.base.loadsirCallback

import com.kingja.loadsir.callback.Callback
import com.zues.ruiyu.bangwoqu.R

/**
 * Created with Android Studio.
 * Description:
 * @author: Wangjianxian
 * @date: 2020/02/25
 * Time: 19:26
 */

class EmptyCallBack : Callback() {
    override fun onCreateView(): Int = R.layout.layout_empty
}