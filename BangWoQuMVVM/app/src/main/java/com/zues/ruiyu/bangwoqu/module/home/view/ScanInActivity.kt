package com.zues.ruiyu.bangwoqu.module.home.view

import android.annotation.SuppressLint
import android.os.*
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chinalwb.slidetoconfirmlib.ISlideListener
import com.google.gson.Gson
import com.google.zxing.Result
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.base.commonUtils.BottomSheetHelper.BottomSheetBehavior
import com.zues.ruiyu.bangwoqu.base.commonUtils.BottomSheetHelper.BottomSheetBehaviorRecyclerManager
import com.zues.ruiyu.bangwoqu.base.commonUtils.InputMethodUtils
import com.zues.ruiyu.bangwoqu.base.commonUtils.SoftKeyboardStateHelper
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssSPHelper
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.addMyDecimalWatcher
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.addWatcher
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.dp2px
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.gone
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.invisible
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.show
import com.zues.ruiyu.bangwoqu.base.eventbus.BangwqMessageEvent
import com.zues.ruiyu.bangwoqu.base.eventbus.BaseDataEventMessage
import com.zues.ruiyu.bangwoqu.base.https.ApiList
import com.zues.ruiyu.bangwoqu.base.https.getApiCode
import com.zues.ruiyu.bangwoqu.base.https.onResponse
import com.zues.ruiyu.bangwoqu.module.home.adpater.ScanResultAdapter
import com.zues.ruiyu.bangwoqu.module.home.data.BulkStorageData
import com.zues.ruiyu.bangwoqu.module.home.data.BulkStorageModel
import com.zues.ruiyu.bangwoqu.module.home.data.PackageInfoResponse
import com.zues.ruiyu.bangwoqu.ocr.camera.view.ScannerFinderView.SCAN_MODE_ALL
import com.zues.ruiyu.bangwoqu.ocr.camera.view.ScannerFinderView.SCAN_MODE_PHONE
import kotlinx.android.synthetic.main.activity_scan_in.*
import kotlinx.android.synthetic.main.activity_scan_in.title_view
import kotlinx.android.synthetic.main.item_scan_result.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_scan_in.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_scan_in.iv_plus
import kotlinx.android.synthetic.main.layout_bottom_sheet_scan_in.iv_reduce
import kotlinx.android.synthetic.main.layout_bulk_storage_success.*
import kotlinx.android.synthetic.main.layout_edit_package_info.*
import kotlinx.android.synthetic.main.layout_scan_phone.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.contentView
import org.jetbrains.anko.toast
import java.util.*


/**
 *@Author liforent
 *@create 2020/12/1 16:24
 *
 * V1.2.4新版入库页面
 *
 * 此处的scanMode与isQrCode耦合，还可以抽.
 * ScanMode 与isQrCode是不一样的概念，ScanMode之后分为全屏和半屏
 */
class ScanInActivity : BaseScannerActivity() {

    companion object {
        const val TYPE_WARN = 201
        const val TYPE_ERROR = 202

        const val TYPE_SUCCESS = 200

        const val STR_UNKNOWN_ADDRESS = "地址不全，未识别村"


        const val BOTTOM_LAYOUT_TYPE_NORMAL = 300    // 全屏扫描时
        const val BOTTOM_LAYOUT_TYPE_FULL_EXPANDED = 302 //查看列表时

        const val PEEK_HEIGHT_COLLAPSED = 58
        const val PEEK_HEIGHT_NORMAL = 162

        private var DECIMAL_DIGITS = 1 //小数位数

    }


    var mBottomSheetHeight = 1  //展开的高度，onCreate之后动态计算
    var mHalfRatio = 0.5f
    private val mEditFullInfoLayoutHeight = 517
    var mKeyBoardHeight = 0
    var isScanPhoneModeEditPhone = false


    var mAdapter = ScanResultAdapter(this)
    private val mBehavior by lazy {
        BottomSheetBehavior.from(findViewById(R.id.nested_scrollview))
    }

    private var curTrackingNumber = ""
    private var curPhoneNumber = ""
    var fee = 1.0f

    var isDeleteClicked = false
    private var clickedPosition = 0
    var isKeyBoardOpen = false // 如果软键盘弹起（正在编辑价格），扫描到数据后不做效，

    override fun getLayoutId(): Int = R.layout.activity_scan_in
    private var isEdit = false //判断是扫描  还是编辑
    private var editPosition = 0 //正在编辑的位置

    private var mHandler = Handler(Looper.getMainLooper()) {
        dealWithResult((it.obj as Result).toString().trim(), mQrCodeViewFinder?.scanMode!!)
        true
    }


    override fun initDataObserver() {
        //单号+手机号才能查询状态，现在是查询状态之前已经添加到列表了，如果不能入库需要从列表中移除
        mViewModel.mQueryPackageInfoBeforeScanInData.observe(this, {
            it.onResponse(mViewModel.loadState, {
                fillUi(it.data)
                restartPreviewDelayed()
            }, {
                if (isEdit) {
                    resetEditStatus()
                } else {
                    if (mAdapter.data.size == 1) {
                        mBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    mAdapter.remove(0)
                }
            })
        })

        mViewModel.mBulkStorageData.observe(this, {
            it.onResponse(mViewModel.loadState, {
                layout_bottom_sheet.gone()
                layout_success.show()
            }, {
                mHandler.postDelayed({
                    progress_bar.gone()
                    slide_to_confirm.reset()
                }, 500)

            })
        })
    }

    @SuppressLint("SetTextI18n")
    override fun initScannerView() {
        mQrCodeViewFinder = findViewById(R.id.qr_code_view_finder)
        mQRCodeViewStub = findViewById(R.id.qr_code_view_stub)
        setScanMode(SCAN_MODE_ALL)
        super.initScannerView()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        dismissLlTips()
        //隐藏编辑号码layout
        dismissScanPhoneBottomLayout()
        dismissEditFullInfoBottomLayout()
        //动态获取bottomSheet高度，计算mHalfRatio
        mHandler.postDelayed({
            mBottomSheetHeight = layout_bottom_sheet.height
            val ratio = PEEK_HEIGHT_NORMAL.dp2px(mContext) * 1.0f / mBottomSheetHeight
            mHalfRatio = if (ratio > 0) {
                ratio
            } else {
                0.5f
            }
        }, 500)
        initFee()
        recy_content.apply {
            layoutManager = LinearLayoutManager(this@ScanInActivity)
            adapter = mAdapter
        }
        mBehavior.isHideable = false
        mBehavior.peekHeight = PEEK_HEIGHT_COLLAPSED.dp2px(mContext)
        mBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset < 0.2) {
                    showBottomLayout(BOTTOM_LAYOUT_TYPE_NORMAL)
                }
                if (slideOffset > 0.8) {
                    showBottomLayout(BOTTOM_LAYOUT_TYPE_FULL_EXPANDED)
                }
            }
        })
        //helper to rule scrolls

        val manager =
            BottomSheetBehaviorRecyclerManager(coordinator_layout, mBehavior, layout_bottom_sheet)
        manager.addControl(recy_content)
        manager.create()

    }


    @SuppressLint("SetTextI18n")
    override fun initScannerEvent() {
        super.initScannerEvent()
        rl_root.setOnClickListener {
            if (mBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                mBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        btn_mark_address.setOnClickListener {
            showMarkAddressDF(0)
        }
        bottom_layout_scan_phone_mode.setOnClickListener {
            // It's empty but works.
        }
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.tv_village -> {
                    if (tv_village.text == "未识别到村") {
                        showMarkAddressDF(position)
                    }
                }
                R.id.iv_delete -> {
                    if (mAdapter.data.size == 1) {
                        mBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    adapter.remove(position)
                }
                R.id.iv_edit -> {
                    val curData = mAdapter.getItem(position) as BulkStorageModel
                    Glide.with(this)
                        .load(curData.logo)
                        .apply(RequestOptions().placeholder(R.drawable.ic_avatar_holder))
                        .into(iv_edit_logo)
                    tv_edit_tracking_number.text = "${curData.com_name} ${curData.tracking_number}"
                    et_edit_phone.setText("${curData.receiver_phone} ${curData.freight}")
                    et_edit_phone.setSelection(11)
                    editPosition = position
                    et_edit_price.setText("${curData.freight}")
                    setEditMode(EditMode.CANCEL)
                    showEditFullInfoBottomLayout()
                    et_edit_phone.requestFocus()
                    InputMethodUtils.openKeyBoard(this, et_edit_phone)
                    layout_bottom_sheet.invisible()
                }
                R.id.tv_delete -> {
                    if (!isDeleteClicked) {
                        view.findViewById<TextView>(R.id.tv_delete).text = "确认删除"
                        isDeleteClicked = true
                        clickedPosition = position
                    } else {
                        if (mAdapter.data.size == 1) {
                            mBehavior.peekHeight = 58.dp2px(mContext)
                        }
                        adapter.remove(clickedPosition)
                        isDeleteClicked = false
                        clickedPosition = 0
                    }
                }
            }
        }

        addKeyBoardListener()
        title_view.setBack {
            confirmQuit()
        }
        tv_back.setOnClickListener {
            quit()
            EventBus.getDefault().post(BangwqMessageEvent(Constant.EVENT_UPDATE_HOME))
        }
        tv_edit_save.setOnClickListener {
            InputMethodUtils.closeKeyBoard(mContext, et_edit_price)
            InputMethodUtils.closeKeyBoard(mContext, et_edit_phone)
            dismissEditFullInfoBottomLayout()
            layout_bottom_sheet.show()
            if (tv_edit_save.text == "保存") {
                curTrackingNumber =
                    (mAdapter.getItem(editPosition) as BulkStorageModel).tracking_number
                curPhoneNumber = et_edit_phone.text.toString()
                ZLog.e("$curTrackingNumber  aaaa  $curPhoneNumber   aaa  $fee")
                if (et_edit_price.text.isNotEmpty()) {
                    et_default_price.setText(et_edit_price.text.toString())
                }
                isEdit = true
                mViewModel.queryPackageStatusBeforeScanIn(curTrackingNumber, curPhoneNumber)

            }

        }


        iv_reduce.setOnClickListener {
            et_default_price.clearFocus()
            if (et_default_price.text.isEmpty()) {
                et_default_price.setText("0.5")
                iv_reduce.setImageResource(R.drawable.ic_scanner_reduce_invalide)
            } else {
                var price = et_default_price.text.toString().toFloat()
                when (price) {
                    in 0f..1f -> {
                        price = 0.5f
                        iv_reduce.setImageResource(R.drawable.ic_scanner_reduce_invalide)
                    }
                    in 1f..100f -> {
                        price = price.toInt().toFloat() - 1
                    }
                }
                et_default_price.setText(price.toString())
            }

        }

        iv_plus.setOnClickListener {
            et_default_price.clearFocus()
            if (et_default_price.text.isEmpty()) {
                et_default_price.setText("0.5")
                iv_reduce.setImageResource(R.drawable.ic_scanner_reduce_invalide)
            } else {
                //
                var price = et_default_price.text.toString().toFloat()
                if (price < 99f) {
                    price = (price.toInt() + 1).toFloat()
                }
                if (price == 99.0f) {
                    iv_plus.setImageResource(R.drawable.ic_scanner_plus_invalide)
                }

                et_default_price.setText(price.toString())

            }


        }



        et_edit_phone.addWatcher {
            setEditMode(EditMode.WELL_DONE)
        }
        et_edit_price.addWatcher {
            setEditMode(EditMode.WELL_DONE)
        }


        et_default_price.addMyDecimalWatcher {
            if (it?.length != 0) {
                val price = it.toString().toFloat()
                //最小值为0.5
                if (price <= 0.5) {
                    if (it?.length == 3 || it!!.equals("0")) {
                        et_default_price.setText("0.5")
                        et_default_price.setSelection(3)
                        iv_reduce.setImageResource(R.drawable.ic_scanner_reduce_invalide)
                    }

                } else if (price > 99f) {
                    et_default_price.setText("99.0")
                    et_default_price.setSelection(4)
                    iv_plus.setImageResource(R.drawable.ic_scanner_plus_invalide)

                } else {
                    iv_reduce.setImageResource(R.drawable.ic_scanner_reduce)
                    iv_plus.setImageResource(R.drawable.ic_scanner_plus)
                }
            }
        }
        et_edit_price.addMyDecimalWatcher {
            if (it?.length != 0) {
                val price = it.toString().toFloat()
                //最小值为0.5
                if (price <= 0.5) {
                    if (it?.length == 3 || it!!.equals("0")) {
                        et_edit_price.setText("0.5")
                        et_edit_price.setSelection(3)
                    }

                } else if (price > 99f) {
                    et_edit_price.setText("99.0")
                    et_edit_price.setSelection(4)

                }
            }
        }

        //号码识别到后监听
        et_str_phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 11) {
                    curPhoneNumber = s.toString()
                    mViewModel.queryPackageStatusBeforeScanIn(curTrackingNumber, curPhoneNumber)
                    setScanMode(SCAN_MODE_ALL)
                    layout_bottom_sheet.show()
                    InputMethodUtils.closeKeyBoard(this@ScanInActivity, et_str_phone)

                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        slide_to_confirm.slideListener = object : ISlideListener {
            override fun onSlideStart() {
            }

            override fun onSlideMove(percent: Float) {
            }

            override fun onSlideCancel() {
            }

            override fun onSlideDone() {
                if (mAdapter.data.size == 0) {
                    toast("入库订单数量不能为零！")
                    slide_to_confirm.reset()
                    return
                }
                progress_bar.show()
//                var mBulkStorageDatas: ArrayList<BulkStorageData> = ArrayList()
//                for (i in mAdapter.data.indices) {
//                    val mBulkData = BulkStorageData(
//                        mAdapter.data[i].receiver_phone,
//                        mAdapter.data[i].tracking_number,
//                        mAdapter.data[i].freight
//                    )
//                    mBulkStorageDatas.add(mBulkData)
//                }
                val body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    Gson().toJson(mAdapter.data)
                )
                mViewModel.bulkStorage(body)
            }
        }


    }


    override fun showErrorMsg(msg: String) {
        showSuccessCallback()
        if (msg.contains("快递已入库")) {
            showTopTips("快递已存在，请勿重复入库", TYPE_WARN)
        } else {
            showTopTips(msg, TYPE_ERROR)
        }
        restartPreviewDelayed()

        if (msg.getApiCode() == ApiList.BULK_STORAGE.toString()) {
            progress_bar.gone()
            slide_to_confirm.reset()
        }
    }

    override fun showSystemErrorMsg(msg: String) {
        showTopTips(msg, TYPE_ERROR)
        if (isEdit) {
            resetEditStatus()
        }

        if (msg.getApiCode() == ApiList.QUERY_PACKAGE_STATUS_BEFORE_SCAN_IN.toString()) {
            if (mAdapter.itemCount > 0) {
                mAdapter.remove(0)
            }
        }
        if (msg.getApiCode() == ApiList.BULK_STORAGE.toString()) {
            mHandler.postDelayed({
                progress_bar.gone()
                slide_to_confirm.reset()
            }, 500)
        }
        restartPreviewDelayed()

    }


    override fun handleDecode(result: Result?) {
        super.handleDecode(result)
        ZLog.e("扫描结果：" + result.toString())
        if (isKeyBoardOpen) {
            restartPreviewDelayed()
            return
        }
        if (null == result) {
            toast("请调整角度再试")
            restartPreview()
        } else {
            val msg = Message.obtain()
            msg.what = 0
            msg.obj = result
            mHandler.sendMessage(msg)

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        //  LitePal.deleteAll(BulkStorageModel::class.java)
        ZssSPHelper.getInstance(mContext).putListData(Constant.DATA_ITEM, mAdapter.data)
//        for (i in mAdapter.data.indices) {
//            mAdapter.data[i].save()
//        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

//        val mRestoredData = LitePal.findAll(BulkStorageModel::class.java)
//        for(i in mRestoredData.indices){
//            ZLog.e(mRestoredData[i].toString())
//        }
        val mRestoredData = ZssSPHelper.getInstance(mContext)
            .getListData(Constant.DATA_ITEM, BulkStorageModel::class.java)
        mAdapter.setNewData(mRestoredData)
        mAdapter.notifyDataSetChanged()
    }


    override fun quit() {
        EventBus.getDefault().post(BangwqMessageEvent(Constant.EVENT_UPDATE_HOME))
        saveFeeToSp()
        finish()

    }

    override fun onBackPressed() {
        confirmQuit()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: Any?) {
        if (event is BaseDataEventMessage) {
            if (Constant.EVENT_COMELETE_ADDRESS == event.message) {
                val curData = mAdapter.getItem(editPosition) as BulkStorageModel
                isEdit = true

                mViewModel.queryPackageStatusBeforeScanIn(
                    curData.tracking_number,
                    curData.receiver_phone
                )
            }
        }
        if (event is BangwqMessageEvent) {
            if (Constant.EVENT_QUIT == event.message) {
                quit()
            }
        }
    }


    private fun fillUi(data: PackageInfoResponse) {
        //现在允许etDefaultPrice被清空，但是需要加一层保护
        if (et_default_price.text.isEmpty() || et_default_price.text.toString() == "0" || et_default_price.text.toString() == "0.") {
            fee = 0.5f
            et_default_price.setText(fee.toString())
        } else {
            fee = et_default_price.text.toString().toFloat()
        }
        var curPrice = 0.5f
        if (data.price_info != null) {
            if (data.price_info!!.price == null) {
                curPrice = fee
            } else {
                curPrice = if (data.price_info!!.price!!.toString() != "0.000") {
                    data.price_info!!.price!!.toFloat()
                } else {
                    fee
                }
            }
        }


        val bulkStorageModel = BulkStorageModel(
            data.phone,
            data.tracking_number,
            data.com_name,
            data.com,
            data.info,
            mUserInfo!!.id.toInt(),
            curPrice,
            data.price_info,
            data.com_logo,
            data.town_code
        )
        if (data.price_info == null) {
            showTopTips(STR_UNKNOWN_ADDRESS, TYPE_WARN)
        } else {
            if (data.price_info!!.name?.isEmpty() == true) {
                showTopTips(STR_UNKNOWN_ADDRESS, TYPE_WARN)
            } else {
                showTopTips(data.price_info!!.name.toString(), TYPE_SUCCESS)
            }
        }
        val changedPosition = if (isEdit) editPosition else 0
        mAdapter.setData(
            changedPosition,
            bulkStorageModel
        )
        mAdapter.notifyDataSetChanged()
        if (isEdit) {
            resetEditStatus()
        } else {
            mBehavior.halfExpandedRatio = mHalfRatio
            mBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED)

        }

    }

    private fun resetEditStatus() {
        isEdit = false
        editPosition = 0
        curTrackingNumber = ""
        curPhoneNumber = ""
    }

    private fun showTopTips(msg: String, type: Int) {
        when (type) {
            TYPE_WARN -> {
                ll_tips.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_bottom_r15_yello, null)
            }
            TYPE_ERROR -> {
                ll_tips.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_bottom_r15_red, null)
            }
            TYPE_SUCCESS -> {
                iv_exclamatory.gone()
                ll_tips.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_bottom_r15_blue, null)
            }
        }
        tv_msg.text = msg
        var lastTime = 2000L
        if (msg == STR_UNKNOWN_ADDRESS) {
            btn_mark_address.show()
            lastTime = 4000L
        }
        ll_tips.show()
        ll_tips.animate().translationY(0f).setDuration(200).start()
        ll_tips.postDelayed({
            dismissLlTips()
        }, lastTime)


    }

    //每次识别到数据后先添加一条数据，如果curPhoneNumber为空，继续扫描，如果curPhoneNumber不为空，调接口查询包裹信息，然后清空当前单号和包裹。
    //此处除了curPhoneNumber，curTrackingNumber，其他值都是为了填充的。（贪省事recyclerView item的数据类型和批量上传的数据类型设为同一个了。）
    private fun addPackageData() {
        mAdapter.addData(
            0,
            BulkStorageModel(
                curPhoneNumber,
                curTrackingNumber,
                "",
                "",
                null,
                mUserInfo!!.id.toInt(),
                0.0f,
                null,
                "",
                null
            )
        )
        recy_content.scrollToPosition(0)
        //如果手机号为空，说明号码未识别，需要手动输入或继续扫描
        if (mBehavior.state == BottomSheetBehavior.STATE_COLLAPSED
        ) {
            mBehavior.halfExpandedRatio = mHalfRatio
            mBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        if (curPhoneNumber.isEmpty()) {
            mQrCodeViewFinder?.scanMode = SCAN_MODE_PHONE
            return
        } else {
            mViewModel.queryPackageStatusBeforeScanIn(curTrackingNumber, curPhoneNumber)
            curPhoneNumber = ""
            curTrackingNumber = ""
        }
    }

    private fun initFee() {
        fee =
            ZssSPHelper.getInstance(this).getSharedPreference(Constant.SP_KEY_FEE, 0.5f).toString()
                .toFloat()
        et_default_price.setText(fee.toString())
        if (et_default_price.text.toString() == "0.5") {
            iv_reduce.setImageResource(R.drawable.ic_scanner_reduce_invalide)
        }
        if (et_default_price.text.toString() == "99") {
            iv_plus.setImageResource(R.drawable.ic_scanner_plus_invalide)
        }

    }


    //每次识别出来后就添加一条数据，
    private fun dealWithResult(resultString: String, scanMode: Int) {
        when (scanMode) {
            SCAN_MODE_ALL -> {
                val arrayCodeAndPhone = resultString.split("###")
                //只检测到条码
                if (arrayCodeAndPhone.size == 1) {
                    curTrackingNumber = arrayCodeAndPhone[0]
                    if (!isOverScanned(curTrackingNumber)) {
                        curPhoneNumber = ""
                        addPackageData()
                        setScanMode(SCAN_MODE_PHONE)
                        ZLog.e("handleRes ult:未检测到号码,strPhones.isEmpty()")
                    }
                } else if (arrayCodeAndPhone.size > 1) {
                    curTrackingNumber = arrayCodeAndPhone[0]
                    if (!isOverScanned(curTrackingNumber)) {
                        val strPhones = arrayCodeAndPhone[1].trim()
                        if (strPhones.length > 10) {
                            //多个号码只取第一个
                            curPhoneNumber = strPhones.substring(0, 11)
                            if (curTrackingNumber.contains(curPhoneNumber)) {
                                curPhoneNumber = ""
                                addPackageData()
                                setScanMode(SCAN_MODE_PHONE)
                                ZLog.e("handleResult:未检测到号码,strCode.contains(strFirstPhone)")
                            } else {
                                addPackageData()
                                setScanMode(SCAN_MODE_ALL)
                            }
                        } else {
                            curPhoneNumber = ""
                            addPackageData()
                            setScanMode(SCAN_MODE_PHONE)
                            ZLog.e("handleResult:未检测到号码,strPhones.isEmpty()")
                        }
                    }
                }

            }
            SCAN_MODE_PHONE -> {
                // textWatcher已经设置了监听
                if (curTrackingNumber.contains(resultString.trim())) {
                    restartPreviewDelayed()
                } else {
                    et_str_phone.setText(resultString.trim())
                }
            }
        }
    }


    private fun saveFeeToSp() {
        fee = if (TextUtils.isEmpty(et_default_price.text.toString())) {
            et_default_price.setText("0.5")
            0.5f
        } else {
            et_default_price.text.toString().toFloat()
        }
        ZssSPHelper.getInstance(this).put(
            Constant.SP_KEY_FEE,
            fee
        )
    }

    //列表中不允许单号重复
    private fun isOverScanned(curTrackingNumber: String): Boolean {
        var isAdded = false
        for (i in mAdapter.data.indices) {
            if (curTrackingNumber == mAdapter.data[i].tracking_number) {
                showTopTips("快递重复扫描", TYPE_WARN)
                isAdded = true
                restartPreviewDelayed()
                break
            }
        }
        return isAdded
    }

    private fun showMarkAddressDF(position: Int) {
        editPosition = position
        val curItemData = mAdapter.getItem(position) as BulkStorageModel
        ScanInMarkAddressDialogFragment().apply {
            val bundle = Bundle()
            bundle.putString(Constant.DATA_PHONE, curItemData.receiver_phone)
            curItemData.town_code?.let { it1 -> bundle.putLong(Constant.DATA_TOWN_CODE, it1) }
            arguments = bundle
        }.show(supportFragmentManager, ScanInMarkAddressDialogFragment::class.qualifiedName)
    }

    private fun dismissLlTips() {
        btn_mark_address.gone()
        ll_tips.invisible()
        ll_tips.animate().translationY(-85.dp2px(mContext).toFloat()).setDuration(200).start()
    }

    private fun showScanPhoneBottomLayout() {
        isScanPhoneModeEditPhone = true
        bottom_layout_scan_phone_mode.show()
        bottom_layout_scan_phone_mode.animate()
            .translationYBy(-PEEK_HEIGHT_NORMAL.dp2px(mContext).toFloat()).setDuration(200).start()
    }

    private fun dismissScanPhoneBottomLayout() {
        isScanPhoneModeEditPhone = false
        bottom_layout_scan_phone_mode.invisible()
        bottom_layout_scan_phone_mode.animate()
            .translationY(PEEK_HEIGHT_NORMAL.dp2px(mContext).toFloat())
    }

    private fun showEditFullInfoBottomLayout() {
        layout_edit_full_info.show()
        layout_edit_full_info.animate()
            .translationYBy(-mEditFullInfoLayoutHeight.dp2px(mContext).toFloat()).setDuration(200)
            .start()
    }

    private fun dismissEditFullInfoBottomLayout() {
        layout_edit_full_info.invisible()
        layout_edit_full_info.animate()
            .translationYBy(mEditFullInfoLayoutHeight.dp2px(mContext).toFloat()).setDuration(200)
            .start()
    }


    fun showBottomLayout(layoutType: Int) {
        when (layoutType) {
            BOTTOM_LAYOUT_TYPE_NORMAL -> {
                cl_top_layout_package_info.show()
                cl_top_layout_packages_list.invisible()
                recy_content.show()
            }
            BOTTOM_LAYOUT_TYPE_FULL_EXPANDED -> {
                cl_top_layout_packages_list.show()
                cl_top_layout_package_info.invisible()
                recy_content.show()
            }

        }
    }

    /**
     * 监听键盘收缩弹出，获取键盘高度，键盘弹出时禁止扫描。
     *
     */
    private fun addKeyBoardListener() {
        mHandler.postDelayed({
            SoftKeyboardStateHelper(contentView?.rootView).addSoftKeyboardStateListener(object :
                SoftKeyboardStateHelper.SoftKeyboardStateListener {
                override fun onSoftKeyboardOpened(keyboardHeightInPx: Int) {
                    ZLog.e("onSoftKeyboardOpened")
                    //todo 扫描手机号码模式下弹出软键盘之后会影响布局，未发现原因,此处暂时加个flag做保护处理
                    if (!isScanPhoneModeEditPhone) {
                        ZLog.e("onSoftKeyboardOpened:!isScanPhoneModeEditPhone")
                        mKeyBoardHeight = keyboardHeightInPx
                        isKeyBoardOpen = true
                        tv_empty.show()
                        val curRatio =
                            (keyboardHeightInPx + 58.dp2px(mContext)) * 1.0f / mBottomSheetHeight
                        if (curRatio in 0f..1f) {
                            mHalfRatio = curRatio
                        } else {
                            mHalfRatio = 0.3f
                        }
                        ZLog.e("$keyboardHeightInPx $curRatio $mBottomSheetHeight")
                        mBehavior.halfExpandedRatio = mHalfRatio
                        //如果弹出软件盘前的状态就是STATE_HALF_EXPANDED，直接设置STATE_HALF_EXPANDED不会生效，下同。
                        mBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        mBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                        et_default_price.setSelection(et_default_price.text.length)
                    }
                }

                override fun onSoftKeyboardClosed() {
                    if (!isScanPhoneModeEditPhone) {
                        isKeyBoardOpen = false
                        tv_empty.gone()
                        mBehavior.halfExpandedRatio = mHalfRatio
                        mBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        mBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                        et_default_price.clearFocus()
                    }

                }
            })
        }, 1000)
    }


    /**
     * 同时扫  或者  只扫手机号码
     */
    @SuppressLint("SetTextI18n")
    private fun setScanMode(mode: Int) {
        when (mode) {
            SCAN_MODE_PHONE -> {
                mQrCodeViewFinder?.setTipsText("请扫描手机号码")
                mQrCodeViewFinder?.scanMode = SCAN_MODE_PHONE
                isQRCode = false
                et_str_phone.text.clear()
                layout_bottom_sheet.invisible()
                showScanPhoneBottomLayout()

                tv_edit_phone_tracking_number.text = "快递单号：$curTrackingNumber"
            }
            SCAN_MODE_ALL -> {
                mQrCodeViewFinder?.setTipsText("请扫描运单")
                mQrCodeViewFinder?.scanMode = SCAN_MODE_ALL
                isQRCode = true
                showBottomLayout(BOTTOM_LAYOUT_TYPE_NORMAL)
                dismissScanPhoneBottomLayout()
            }
        }
        restartPreviewDelayed()
    }

    private fun setEditMode(mode: EditMode) {
        when (mode) {
            EditMode.WELL_DONE -> {
                tv_edit_save.text = "保存"
                tv_edit_save.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_scan_in_edit_save, null)
            }
            EditMode.CANCEL -> {
                tv_edit_save.text = "取消"
                tv_edit_save.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_scan_in_edit_cancel, null)
            }
        }
    }

    private fun confirmQuit() {
        if (mAdapter.itemCount > 0) {
            ScanInQuitDialogFragment().show(
                supportFragmentManager,
                ScanInQuitDialogFragment::class.qualifiedName
            )
        } else {
            quit()
        }
    }

    enum class EditMode {
        WELL_DONE,
        CANCEL

    }

}



