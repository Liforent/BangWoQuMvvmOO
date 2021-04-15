package com.zues.ruiyu.bangwoqu.module.home.view

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
import com.zues.ruiyu.bangwoqu.base.DialogHelper.showEditDialog
import com.zues.ruiyu.bangwoqu.base.eventbus.BangwqMessageEvent
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssDeviceHelper
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.dp2px
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.gone
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.invisible
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.show
import com.zues.ruiyu.bangwoqu.base.https.onResponse
import com.zues.ruiyu.bangwoqu.module.account.view.EditInfoDialogFragment
import com.zues.ruiyu.bangwoqu.module.home.adpater.WarehousingAdapter
import com.zues.ruiyu.bangwoqu.ocr.camera.view.ScannerFinderView
import kotlinx.android.synthetic.main.activity_scanner_out.*
import kotlinx.android.synthetic.main.activity_scanner_out.iv_exclamatory
import kotlinx.android.synthetic.main.activity_scanner_out.ll_tips
import kotlinx.android.synthetic.main.activity_scanner_out.title_view
import kotlinx.android.synthetic.main.activity_scanner_out.tv_msg
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast

/**
 *@Author liforent
 *@create 2020/11/10 10:28
 */
class ScannerOutActivity : BaseScannerActivity() {

    private val mWarehousingAdapter =
        WarehousingAdapter()
    private var mTrackingNumber = ""
    private var isSelf = Constant.IS_SELF

    override fun getLayoutId() = R.layout.activity_scanner_out


    override fun initDataObserver() {
        mViewModel.mQueryPackageStatusBeforeScanOutData.observe(this, {
            it.onResponse(mViewModel.loadState, {
                ScanOutDialog().apply {
                    arguments = Bundle()
                    arguments?.putInt(Constant.DATA_SCAN_OUT_MODE, isSelf)
                }.show(supportFragmentManager, ScanOutDialog::class.qualifiedName)
            }, {
                clearPackageInfo()
                restartPreviewDelayed()
                isQRCode = true
            })
        })

        mViewModel.mScanOutData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                showSuccessTips()
                restartPreviewDelayed()
                isQRCode = true
            }
        })

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


    override fun initScannerView() {
        mQrCodeViewFinder = findViewById(R.id.qr_code_view_finder)
        mQRCodeViewStub = findViewById(R.id.qr_code_view_stub)
        super.initScannerView()
        ll_tips.invisible()
        ll_tips.animate().translationY(-85.dp2px(mContext).toFloat()).setDuration(200).start()
        mQrCodeViewFinder?.scanMode = ScannerFinderView.SCAN_MODE_V1_2
        title_view.setTitleText("扫描出库")
        isQRCode = true
        initRecy()
        initPackageInfoLP()
        initScanOutMode()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun initScannerEvent() {
        title_view.setBack {
            quit()
        }
        btn_scan_out_self.setOnClickListener {
            btn_scan_out_self.isSelected = true
            btn_scan_out_not_self.isSelected = false
            isSelf = Constant.IS_SELF
        }
        btn_scan_out_not_self.setOnClickListener {
            btn_scan_out_self.isSelected = false
            btn_scan_out_not_self.isSelected = true
            isSelf = Constant.IS_NOT_SELF
        }
        title_view.setRightTextVisibiliy(true)
        title_view.setRightTextOnclickListener {
            showEditDialog(
                supportFragmentManager,
                "手动出库",
                "请输入快递单号",
                object : EditInfoDialogFragment.DFOnClickListener {
                    override fun onLeftBtnClicked() {

                    }

                    override fun onRightBtnClicked(etContent: String?) {
                        if (etContent?.isNotEmpty() == true) {
                            scanOut(etContent)
                        } else {
                            toast("订单号不能为空！")
                        }
                    }
                }
            )
        }
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

    override fun quit() {
        EventBus.getDefault().post(BangwqMessageEvent(Constant.EVENT_UPDATE_HOME))
        finish()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
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
            // TODO: 2020/12/7  此处有概率识别到手机号码，此处只是做了层保护，可以从源头优化
            mTrackingNumber = if (result.toString().contains("###")) {
                result.toString().split("###")[0]
            } else {
                result.toString()
            }
            queryPackageStatus(mTrackingNumber, isSelf)

        }
        isQRCode = true
    }

    private fun queryPackageStatus(trackingNumber: String, isSelf: Int) {
        mViewModel.queryPackageStatusBeforeScanOut(trackingNumber, isSelf)

    }

    private fun scanOut(tracking: String) {
        mViewModel.scanOut(tracking, isSelf)
    }


    private fun clearPackageInfo() {
        ll_package_info.gone()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun checkScanOutMode(event: Any?) {
        if (event is BangwqMessageEvent) {
            if (Constant.EVENT_SUBMIT == event.message) {
                scanOut(mTrackingNumber)
                clearPackageInfo()
                restartPreviewDelayed()
                isQRCode = true
            }
            if (Constant.EVENT_CANCEL == event.message) {
                clearPackageInfo()
                restartPreviewDelayed()
                isQRCode = true
            }
        }
    }


    private fun initScanOutMode() {
        ScanOutChooseModeDialogFragment().apply {
            this.setModeListener(object : ScanOutChooseModeDialogFragment.ModeSelectListener {
                override fun onModeSelected(mode: Int) {
                    initMode(mode)
                }
            })
            show(supportFragmentManager, ScanOutChooseModeDialogFragment::class.qualifiedName)
        }
    }

    private fun initMode(mode: Int) {
        runOnUiThread {
            isSelf = mode
            if (mode == Constant.IS_SELF) {
                btn_scan_out_self.isSelected = true
                btn_scan_out_not_self.isSelected = false
            } else {
                btn_scan_out_self.isSelected = false
                btn_scan_out_not_self.isSelected = true
            }
        }

    }


    private fun initPackageInfoLP() {
        val screenWidth = ZssDeviceHelper.getScreenWidth(mContext)
        val recWidth: Int = screenWidth * 4 / 5    //扫描框宽度
        val recHeight = (recWidth * 1.0 / 315 * 100).toInt()    //扫描框高度
        val params = RelativeLayout.LayoutParams(recWidth, RelativeLayout.LayoutParams.WRAP_CONTENT)
        val top = 150.dp2px(mContext) //扫描框top
        val llPaddingTop = 32.dp2px(mContext) //价格栏目到扫描框padding
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        params.setMargins(0, top + recHeight + llPaddingTop, 0, 0)
        ll_package_info.layoutParams = params
        ll_package_info.gone()
    }


    @SuppressLint("SetTextI18n")
    private fun initRecy() {
        recy_detail_content.adapter = mWarehousingAdapter
        recy_detail_content.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mWarehousingAdapter.setNewData(null)

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


}
