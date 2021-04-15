package com.zues.ruiyu.bangwoqu.module.home.view

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.commonUtils.BottomSheetHelper.BottomSheetBehavior
import com.zues.ruiyu.bangwoqu.base.commonUtils.BottomSheetHelper.BottomSheetBehaviorRecyclerManager
import com.zues.ruiyu.bangwoqu.base.eventbus.BaseDataEventMessage
import com.zues.ruiyu.bangwoqu.base.https.onResponse
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleDialogFragment
import com.zues.ruiyu.bangwoqu.module.home.HomeViewModel
import com.zues.ruiyu.bangwoqu.module.home.adpater.AreaInfoAdapter
import com.zues.ruiyu.bangwoqu.module.home.data.AreaInfoResponse
import com.zues.ruiyu.bangwoqu.module.home.data.CompleteReceiverInfoModel
import kotlinx.android.synthetic.main.df_scan_in_mark_address.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_scan_in.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor

/**
 *@Author liforent
 *@create 2020/12/10 16:16
 */
class ScanInMarkAddressDialogFragment : BaseLifeCycleDialogFragment<HomeViewModel>() {
    private val mPhone by lazy {
        arguments?.getString(Constant.DATA_PHONE, "")
    }
    private val mTownCode by lazy {
        arguments?.getLong(Constant.DATA_TOWN_CODE, -1L)
    }

    private val mContext by lazy {
        requireContext()
    }
    private var mAdapter = AreaInfoAdapter()
    private var choosedVillageCode = 0L

    private val mBehavior by lazy {
        BottomSheetBehavior.from(nested_scrollview_mark_address)
    }


    override fun getContentLayoutId(): Int = R.layout.df_scan_in_mark_address

    override fun handleViewCreate(view: View) {
        super.handleViewCreate(view)
        view.findViewById<TextView>(R.id.tv_phone).text = mPhone
        mBehavior.peekHeight = 0
        val manager =
            BottomSheetBehaviorRecyclerManager(
                coordinator_layout,
                mBehavior,
                nested_scrollview_mark_address
            )
        manager.addControl(recyclerview)
        manager.create()

    }

    override fun initEvent(view: View) {
        super.initEvent(view)
        view.findViewById<ImageView>(R.id.iv_quit)?.setOnClickListener {
            mBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        view.findViewById<LinearLayout>(R.id.ll_choose_place).setOnClickListener {
            if (mTownCode == -1L || mTownCode == null) {
                toast("地址获取失败了！请联系管理员~")
            } else {
                mViewModel.queryVillageList(mTownCode!!)
            }

        }
        view.findViewById<ImageView>(R.id.iv_cancel).setOnClickListener {
            dismiss()
        }
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        btnSave.setOnClickListener {
            if (tv_address.text.toString() == "点击选择地址") {
                toast("请选择地址")
            } else {
                showLoading()
                mViewModel.completeRecevierInfo(
                    CompleteReceiverInfoModel(
                        mPhone!!,
                        tv_address.text.toString(),
                        choosedVillageCode
                    )
                )
            }
        }
    }

    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mQueryVillageListData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                fillUi(it.data)
                mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })
        mViewModel.mCompleteReceiverInfoModel.observe(this) {
            it.onResponse(mViewModel.loadState) {
                toast("修改成功！${tv_address.text}")
                EventBus.getDefault().post(
                    BaseDataEventMessage(
                        Constant.EVENT_COMELETE_ADDRESS,
                        tv_address.text.toString()
                    )
                )
                Handler(Looper.getMainLooper()).postDelayed({dismiss()},200)

            }
        }
    }


    private fun fillUi(data: List<AreaInfoResponse>) {
        recyclerview.layoutManager = LinearLayoutManager(mContext)
        recyclerview.adapter = mAdapter
        mAdapter.setNewData(data)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val textView = view.findViewById<TextView>(R.id.tv_completed_village)
            textView.setTextColor(Color.parseColor("#EF2937"))
            tv_address.text = textView.text
            choosedVillageCode = (adapter.getItem(position) as AreaInfoResponse).id
            tv_address.textColor = resources.getColor(R.color.black66, null)
            Handler(Looper.getMainLooper()).postDelayed({
                mBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }, 200)
        }
    }


}
