package com.zues.ruiyu.bangwoqu.module.home.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleDialogFragment
import com.zues.ruiyu.bangwoqu.module.home.HomeViewModel
import com.zues.ruiyu.bangwoqu.module.home.adpater.ScanResultAdapter
import com.zues.ruiyu.bangwoqu.module.home.data.ScanResultModel
import kotlinx.android.synthetic.main.df_scan_result.*
import kotlinx.android.synthetic.main.df_scan_result.view.*
import org.jetbrains.anko.db.NULL
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 *@Author liforent
 *@create 2020/11/26 16:35
 */
class ScanResultDialog : BaseLifeCycleDialogFragment<HomeViewModel>() {
    var scanPhoneListener: OnScanPhoneListener? = null
    //var mAdapter = ScanResultAdapter("3")
    var mData: ScanResultModel? = null
    private var isFullScreenMode = true

    private val resultStr by lazy {
        arguments?.getString(Constant.DATA_SCAN_RESULT)
    }

    override fun setAlpha() {
        mAlpha = 0f
    }


    override fun getContentLayoutId(): Int = R.layout.df_scan_result

    @SuppressLint("SetTextI18n")
    override fun handleViewCreate(view: View) {
        super.handleViewCreate(view)
        recy_content.layoutManager = LinearLayoutManager(requireContext())
       // recy_content.adapter = mAdapter
       // mAdapter.setNewData(null)
        initMVIEW(view)
        initScanMode()
    }


    private fun initMVIEW(view: View) {
        showResult(resultStr!!)
        view.cl_root.onClick {
            dismiss()
        }
        view.tv_scan_mode.setOnClickListener {
            scanPhoneListener?.onScanPhone()
            isFullScreenMode = !isFullScreenMode
            initScanMode()

        }
    }

    private fun initScanMode() {
        if (isFullScreenMode) {
            view?.tv_scan_mode?.text = "扫描模式：全屏"
        } else {
            view?.tv_scan_mode?.text = "扫描模式：手机号码"
        }
    }


    fun setListener(scanPhoneListener: OnScanPhoneListener) {
        this.scanPhoneListener = scanPhoneListener
    }

    //
    fun showResult(text: String) {
//        ZLog.e("todo:" + text)
//        // TODO: 2020/11/27 此处为什么必须清空？？？？
//        mData = null
//        val a = text.split("###")
//        if (a.size > 1) {
//          //  mData = ScanResultModel(a[0], a[1])
//        } else {
//           // mData = ScanResultModel(a[0], "未知号码")
//        }
//        ZLog.e(mData.toString())
//        mAdapter.addData(0, mData!!)
//        recy_content.scrollToPosition(0)


    }

    fun fillPhoneNumber(text: String?) {
//        var curData = mAdapter.getItem(0) as ScanResultModel
//        if (text != "") {
//            curData.phoneNumber = text.toString()
//        }
//        mAdapter.setData(0, curData)
//        mAdapter.notifyDataSetChanged()
    }

    interface OnScanPhoneListener {
        fun onScanPhone()
    }


}