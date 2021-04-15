package com.zues.ruiyu.bangwoqu.ocr.camera.decode

import android.os.Handler
import android.os.Looper
import com.zues.ruiyu.bangwoqu.module.home.view.BaseScannerActivity

/**
 *@Author liforent
 *@create 2020/12/17 14:48
 */
class OcrDecodeThread(private val mActivity: BaseScannerActivity) : Thread() {
    public lateinit var mHandler: Handler

    override fun run() {
        super.run()
        Looper.prepare()
        mHandler = DecodeHandler(mActivity)
        Looper.loop()
    }
}