package com.zues.ruiyu.bangwoqu.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.widget.RelativeLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.Result
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.Constant.TIPS_TYPE_SUCCESS
import com.zues.ruiyu.bangwoqu.base.Constant.TIPS_TYPE_WARN
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.base.eventbus.BangwqMessageEvent
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssDeviceHelper
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.dp2px
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.gone
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.invisible
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.show
import com.zues.ruiyu.bangwoqu.base.https.onResponse
import com.zues.ruiyu.bangwoqu.module.home.adpater.WarehousingAdapter
import com.zues.ruiyu.bangwoqu.module.home.view.BaseScannerActivity
import com.zues.ruiyu.bangwoqu.ocr.camera.view.ScannerFinderView
import kotlinx.android.synthetic.main.activity_scanner_out.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *@Author liforent
 *@create 2020/11/10 10:28
 *
 * 直接继承了ScannerOutActivity,很多方法都是覆写ScannerOutActivity里的
 *
 * ...
 * 需要解耦，太多的重写导致比较混乱。
 *
 */
class TestScannerActivity : BaseScannerActivity() {

    private val mWarehousingAdapter =
        WarehousingAdapter()
    private var mTrackingNumber = ""
    override fun getLayoutId() = R.layout.activity_scanner_out
    private var isSelf = Constant.IS_SELF


    override fun initDataObserver() {
        mViewModel.mQueryPackageStatusBeforeScanOutData.observe(this, {
           // scanOut(mTrackingNumber)
        })

        mViewModel.mScanOutData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                showSuccessTips()
                restartPreviewDelayed()
                isQRCode = true
            }
        })

    }

    override fun showErrorMsg(msg: String) {
        restartPreviewDelayed()
        isQRCode = true
        showTopTips(msg, TIPS_TYPE_WARN)

    }

    override fun showSystemErrorMsg(msg: String) {
        showTopTips(msg, TIPS_TYPE_WARN)
        restartPreviewDelayed()
        isQRCode = true
    }



    override fun handleDecode(result: Result?) {
        super.handleDecode(result)
        if (null == result) {
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
                restartPreviewDelayed()
            }
        } else {
            ZLog.e("testmy:getResult"+result)
            // TODO: 2020/12/7  此处有概率识别到手机号码，此处只是做了层保护，可以从源头优化
            mTrackingNumber = if (result.toString().contains("###")) {
                result.toString().split("###")[0]
            } else {
                result.toString()
            }
           // queryPackageStatus(mTrackingNumber, isSelf)

        }
        isQRCode = true
    }








    override fun initScannerView() {
        mQrCodeViewFinder = findViewById(R.id.qr_code_view_finder)
        mQRCodeViewStub = findViewById(R.id.qr_code_view_stub)
        super.initScannerView()
        ll_tips.invisible()
        mQrCodeViewFinder?.scanMode = ScannerFinderView.SCAN_MODE_ALL
        isQRCode = false

    }










    override fun quit() {
        EventBus.getDefault().post(BangwqMessageEvent(Constant.EVENT_UPDATE_HOME))
        finish()

    }


    private fun showSuccessTips() {
        showTopTips("出库成功", TIPS_TYPE_SUCCESS)
    }


    private fun showTopTips(msg: String, type: Int) {
        when (type) {
            TIPS_TYPE_SUCCESS -> {
                iv_exclamatory.setImageResource(R.drawable.ic_green_check)
                ll_tips.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_bottom_r15_green, null)
            }
            TIPS_TYPE_WARN -> {
                iv_exclamatory.setImageResource(R.drawable.ic_scan_in_exclamatory)
                if (msg.contains("快递为代取") || msg.contains("快递为自取")) {
                    iv_exclamatory.gone()
                    ll_tips.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.bg_bottom_r15_blue, null)
                } else {
                    ll_tips.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.bg_bottom_r15_yello, null)
                }

            }
        }
        tv_msg.text = msg
        ll_tips.show()
        ll_tips.animate().translationY(0f).setDuration(200).start()
        ll_tips.postDelayed({
            ll_tips.invisible()
            ll_tips.animate().translationY(-85.dp2px(mContext).toFloat()).start()
        }, 2000)


    }

    override fun onDestroy() {
        super.onDestroy()
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
    }

}
