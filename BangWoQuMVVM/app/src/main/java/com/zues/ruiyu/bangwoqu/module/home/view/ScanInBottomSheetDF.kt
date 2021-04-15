package com.zues.ruiyu.bangwoqu.module.home.view

import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssDeviceHelper
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.dp2px
import com.zues.ruiyu.bangwoqu.base.https.onResponse
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleBottomSheetDialogFragment
import com.zues.ruiyu.bangwoqu.module.home.HomeViewModel
import com.zues.ruiyu.bangwoqu.module.home.data.ScanResultModel

/**
 *@Author liforent
 *@create 2020/12/1 16:16
 *
 */
class ScanInBottomSheetDF(val listener: StatesListener?) :
    BaseLifeCycleBottomSheetDialogFragment<HomeViewModel>() {

    //var mAdapter = ScanResultAdapter("3")
    var mData: ScanResultModel? = null
    private val mContext by lazy {
        requireContext()
    }
    var curTrackingNumber = ""
    var curPhoneNumber = ""
    var isHalfExpended = false
    var isInsert = true //insert or update
    private var nestedScrollView: NestedScrollView? = null
    override fun getLayoutId(): Int = R.layout.layout_bottom_sheet_scan_in


    override fun initView(view: View) {
        view.findViewById<RecyclerView>(R.id.recy_content)?.apply {
            layoutManager = LinearLayoutManager(requireContext())
           // adapter = mAdapter
        }
        isCancelable = false
        val height = ZssDeviceHelper.getScreenHeight(mContext) - 70.dp2px(mContext)
        nestedScrollView = view.findViewById(R.id.nested_scrollview)
//        nestedScrollView?.layoutParams =
//            CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, height)
//        mAdapter.setNewData(null)
//        mAdapter.setOnItemChildClickListener { adapter, mView, position ->
//            when (mView.id) {
//                R.id.iv_delete -> {
//
//                }
//                R.id.iv_edit -> {
//
//                }
//            }
//        }
//        val mBehavior = BottomSheetBehavior.from(nestedScrollView!!)
//        mBehavior.peekHeight = 158.dp2px(mContext)
//        mBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                ZLog.e("onStateChanged")
//                // bottomSheet.layoutParams =
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                ZLog.e("onSlide")
//            }
//        })


    }

    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mQueryPackageInfoBeforeScanInData.observe(this, {
            it.onResponse(mViewModel.loadState, {
                // TODO: 2020/12/2 needs logo  and error tips
                val mComLogo = it.data.com
                val price = it.data.price_info
              //  fillUi(mComLogo, price)
                ZLog.e(it.data.toString())
            }, {

            })
        }

        )
    }

//    private fun fillUi(mComLogo: String, price: PriceInfo?) {
//        if (isInsert) {
//            mAdapter.addData(0, ScanResultModel(curTrackingNumber, curPhoneNumber, mComLogo, price))
//            view?.findViewById<RecyclerView>(R.id.recy_content)?.scrollToPosition(0)
//            curPhoneNumber = ""
//            curTrackingNumber = ""
//            val mBehavior = BottomSheetBehavior.from(nestedScrollView!!)
//            mBehavior.halfExpandedRatio = 0.6f
//            showBottomSheet()
//        } else {
//            mAdapter.setData(0, ScanResultModel(curTrackingNumber, curPhoneNumber, mComLogo, price))
//            mAdapter.notifyDataSetChanged()
//            curPhoneNumber = ""
//            curTrackingNumber = ""
//            isInsert = true
//        }
//
//    }


    fun showBottomSheet() {
        if (!isHalfExpended) {
            val mBehavior = BottomSheetBehavior.from(nestedScrollView!!)
            mBehavior.halfExpandedRatio = 0.6f
            mBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            isHalfExpended = true
        }

    }




}

interface StatesListener {
    fun onStatesChanged(states: String)
}