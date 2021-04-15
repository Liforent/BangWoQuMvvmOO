package com.zues.ruiyu.bangwoqu.module.home.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.zxing.Result
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.ocr.camera.view.ScannerFinderView.SCAN_MODE_ALL
import com.zues.ruiyu.bangwoqu.ocr.camera.view.ScannerFinderView.SCAN_MODE_PHONE
import kotlinx.android.synthetic.main.activity_test_scanner.*
import kotlinx.android.synthetic.main.layout_empty.tv_text
import org.jetbrains.anko.toast

/**
 *@Author liforent
 *@create 2020/11/25 11:30
 */
class TestScannerActivity : BaseScannerActivity() {

    private var mResultDialog: ScanResultDialog? = null
    private var lastTime = 0

    private val mHandler = Handler(Looper.getMainLooper()) {
        when (it.what) {
            0 -> {

                iv_pic.setImageBitmap((it.obj as Result).bitmap)
                if (mQrCodeViewFinder?.scanMode == SCAN_MODE_ALL) {
                    dealWithResult((it.obj as Result).toString())
                } else if (mQrCodeViewFinder?.scanMode == SCAN_MODE_PHONE) {
                    val resultString = (it.obj as Result).toString().trim()
                    ZLog.e("扫描号码结果：$resultString")
                    if (resultString.length == 11) {
                        //tv_text.text = resultString
                        mResultDialog?.fillPhoneNumber(resultString)
                        if (mQrCodeViewFinder?.scanMode == SCAN_MODE_PHONE) {
                            setScanMode(SCAN_MODE_ALL)
                        }
                    }

                }
            }
            else -> {

            }
        }
        true
    }

    private fun dealWithResult(resultString: String) {
        toast(resultString)
        ZLog.e(resultString)
        restartPreviewDelayed()

//        val arrayCodeAndPhone = resultString.split("###")
//        var strCode = ""
//        var strPhones = ""
//        var strFirstPhone = ""
//        //只检测到条码
//        if (arrayCodeAndPhone.size == 1) {
//            strCode = arrayCodeAndPhone[0]
//            //  tv_text.text = "未检测到号码"
//            setScanMode(SCAN_MODE_PHONE)
//        } else if (arrayCodeAndPhone.size > 1) {
//
//            strCode = arrayCodeAndPhone[0]
//            strPhones = arrayCodeAndPhone[1]
//
//            if (strPhones.isEmpty()) {
//                strFirstPhone = ""
//                setScanMode(SCAN_MODE_PHONE)
//                ZLog.e("handleResult:未检测到号码,strPhones.isEmpty()")
//            } else if (strPhones.trim().length == 11) {
//                //一个号码
//                strFirstPhone = strPhones.trim()
//
//
//            }
//
//        } else {
//            return
//        }
    }


    private fun setScanMode(mode: Int) {
        when (mode) {
            SCAN_MODE_PHONE -> {
                mQrCodeViewFinder?.setTipsText("请扫描手机号码")
                mQrCodeViewFinder?.scanMode = SCAN_MODE_PHONE
                isQRCode = false
            }
            SCAN_MODE_ALL -> {
                mQrCodeViewFinder?.setTipsText("请将条码对准扫描框")
                mQrCodeViewFinder?.scanMode = SCAN_MODE_ALL
                isQRCode = true
            }
            else -> {
                mQrCodeViewFinder?.setTipsText("请将条码对准扫描框")
                mQrCodeViewFinder?.scanMode = SCAN_MODE_ALL
                isQRCode = true
            }
        }
      restartPreviewDelayed()
    }


    override fun initScannerEvent() {
        super.initScannerEvent()
        title_view.setBack {
            quit()

        }
    }

    override fun initDataObserver() {
    }

    override fun getLayoutId() = R.layout.activity_test_scanner

    override fun initScannerView() {
        mQrCodeViewFinder = findViewById(R.id.qr_code_view_finder)
        mQRCodeViewStub = findViewById(R.id.qr_code_view_stub)
        setScanMode(SCAN_MODE_ALL)
        super.initScannerView()
    }


    override fun handleDecode(result: Result?) {
        super.handleDecode(result)
        if (null == result || result.toString() == "") {
            mDecodeManager.showCouldNotReadQrCodeFromScanner(
                this
            ) { restartPreview() }
        } else {
            // successSound.start()
            handleResult(result)

        }
    }

    private fun handleResult(result: Result) {
        if (TextUtils.isEmpty(result.text)) {
            mDecodeManager.showCouldNotReadQrCodeFromScanner(
                this
            ) {
                restartPreview()
            }
        } else {
            val msg = Message.obtain()
            msg.what = 0
            msg.obj = result
            mHandler.sendMessage(msg)
            ZLog.e("handleResult:$result")
        }
    }

}