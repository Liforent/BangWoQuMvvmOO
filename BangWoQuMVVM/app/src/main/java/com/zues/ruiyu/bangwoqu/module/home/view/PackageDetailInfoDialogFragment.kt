package com.zues.ruiyu.bangwoqu.module.home.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gyf.immersionbar.ImmersionBar
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.gone
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.show
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleDialogFragment
import com.zues.ruiyu.bangwoqu.module.home.HomeViewModel
import com.zues.ruiyu.bangwoqu.module.home.adpater.WarehousingAdapter
import com.zues.ruiyu.bangwoqu.module.home.data.PackagesListResponse
import kotlinx.android.synthetic.main.df_package_detail_info.*
import kotlinx.android.synthetic.main.df_package_detail_info.title_view
import org.jetbrains.anko.textColor

/**
 *@Author liforent
 *@create 2020/12/15 10:42
 */
class PackageDetailInfoDialogFragment : BaseLifeCycleDialogFragment<HomeViewModel>() {
    private val mContext by lazy {
        requireContext()
    }
    private val mWarehousingAdapter =
        WarehousingAdapter()

    private val mPackageInfo by lazy {
        arguments?.getSerializable(Constant.DATA_ITEM) as PackagesListResponse
    }

    override fun getContentLayoutId(): Int = R.layout.df_package_detail_info
    override fun handleViewCreate(view: View) {
        super.handleViewCreate(view)
        ImmersionBar.with(this)
            .statusBarColor(R.color.white)
            .autoDarkModeEnable(true, 0.2f)
            .init()
        initPackageInfo()
    }


    override fun initEvent(view: View) {
        super.initEvent(view)
        title_view.setBack {
            dismiss()
        }
        ll_call.setOnClickListener {
            //call()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initPackageInfo() {
        //初始化默认item状态
        tv_status.textColor = Color.parseColor("#007DFF")
        tv_order_time.show()


        if (mPackageInfo.is_self == 1) {
            title_view.setTitleText("自取详情")
        } else {
            // TODO: 2020/12/16 后台默认把没有选择自取或代取的订单的状态设定为代取，直接用来作标题不妥
            title_view.setTitleText("订单详情")
        }
        if (mPackageInfo.create_time.isNullOrEmpty()) {
            tv_order_time.gone()
        }else{
            //todo 此处为入库事件，并非设计图上的下单时间
            tv_order_time.text = mPackageInfo.create_time.replace("T"," ").substring(0,19)
        }
        when (mPackageInfo.status) {
            Constant.PACKAGE_DETAIL_STATUS_WAREHOUSE -> {
                tv_status.text = "已入库"
                tv_status.textColor = Color.parseColor("#007DFF")

            }
            Constant.PACKAGE_DETAIL_STATUS_WAIT_FOR_COURIER -> {
                tv_status.text = "待抢单"
                tv_status.textColor = Color.parseColor("#FF0F0F")

            }
            Constant.PACKAGE_DETAIL_STATUS_PICK_UP_ORDER -> {
                tv_status.text = "取件中"
                tv_status.textColor = Color.parseColor("#3271F6")
            }
            Constant.PACKAGE_DETAIL_STATUS_DELIVERING -> {
                tv_status.text = "派送中"
                tv_status.textColor = Color.parseColor("#111111")

            }
            Constant.PACKAGE_DETAIL_STATUS_DELIVERED -> {
                tv_status.text = "已送达"
                tv_status.textColor = Color.parseColor("#111111")

            }
            Constant.PACKAGE_DETAIL_STATUS_SIGNED -> {
                tv_status.text = "已签收"
                tv_status.textColor = Color.parseColor("#00B276")

            }
        }
        tv_tracking_number.text = mPackageInfo.tracking_number
        //未注册用户可能姓名和地址为null
        if (mPackageInfo.real_name.isNullOrEmpty()) {
            tv_receiver_name.text = "暂无姓名"
        } else {
            tv_receiver_name.text = "${mPackageInfo.real_name}"
        }
        if (mPackageInfo.default_address.isNullOrEmpty() && mPackageInfo.village.isNullOrEmpty()) {
            tv_receiver_address.text = "暂无地址"
        } else {
            if (mPackageInfo.default_address.isNullOrEmpty()) {
                tv_receiver_address.text = "${mPackageInfo.village}"
            } else {
                tv_receiver_address.text = "${mPackageInfo.default_address}"
            }
        }
        ZLog.e(mPackageInfo.toString())
        if (mPackageInfo.com_name.isNullOrEmpty()) {
            tv_com_name.text = "单号："
        } else {
            tv_com_name.text = mPackageInfo.com_name
        }

        tv_receiver_phone.text = mPackageInfo.receiver_phone
        Glide.with(mContext).load(mPackageInfo.com_logo)
            .apply(RequestOptions().placeholder(R.drawable.ic_avatar_holder)).into(iv_com_logo)
        recyclerview_info.layoutManager = LinearLayoutManager(mContext)
        recyclerview_info.adapter = mWarehousingAdapter
        mWarehousingAdapter.setNewData(mPackageInfo.info)
        mWarehousingAdapter.notifyDataSetChanged()

    }
}