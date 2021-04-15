package com.zues.ruiyu.bangwoqu.module.home.adpater

import android.graphics.Color
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.module.home.data.PackagesListResponse
import kotlinx.android.synthetic.main.df_package_detail_info.*
import org.jetbrains.anko.textColor

/**
 *@Author liforent
 *@create 2020/12/14 16:12
 */
class SearchPackageInfoAdapter :
    BaseQuickAdapter<PackagesListResponse, BaseViewHolder>(R.layout.item_search_package, null) {
    override fun convert(helper: BaseViewHolder, item: PackagesListResponse?) {
        helper.setText(R.id.tv_tracking_number, "${item?.tracking_number}")
        val tvStatus =  helper.itemView.findViewById<TextView>(R.id.tv_status)
        when (item?.status) {
            Constant.PACKAGE_DETAIL_STATUS_WAREHOUSE -> {
                tvStatus.text = "已入库"
                tvStatus.textColor = Color.parseColor("#007DFF")

            }
            Constant.PACKAGE_DETAIL_STATUS_WAIT_FOR_COURIER -> {
                tvStatus.text = "待抢单"
                tvStatus.textColor = Color.parseColor("#FF0F0F")

            }
            Constant.PACKAGE_DETAIL_STATUS_PICK_UP_ORDER -> {
                tvStatus.text = "取件中"
                tvStatus.textColor = Color.parseColor("#3271F6")
            }
            Constant.PACKAGE_DETAIL_STATUS_DELIVERING -> {
                tvStatus.text = "派送中"
                tvStatus.textColor = Color.parseColor("#111111")

            }
            Constant.PACKAGE_DETAIL_STATUS_DELIVERED -> {
                tvStatus.text = "已送达"
                tvStatus.textColor = Color.parseColor("#111111")

            }
            Constant.PACKAGE_DETAIL_STATUS_SIGNED -> {
                tvStatus.text = "已签收"
                tvStatus.textColor = Color.parseColor("#00B276")

            }
        }
        //可能无配送员信息
        if (item?.courier.isNullOrEmpty()) {
            helper.setText(R.id.tv_deliver, "暂无配送员抢单")
        } else {
            helper.setText(
                R.id.tv_driver, "${item?.courier} ${item?.courier_phone}"
            )
        }
        if (item?.com_name.isNullOrEmpty()) {
            helper.setText(R.id.tv_com_name, "单号：")
        } else {
            helper.setText(R.id.tv_com_name, "${item?.com_name}")
        }
        //未注册用户可能姓名和地址为null
        if (item?.real_name.isNullOrEmpty()) {
            helper.setText(R.id.tv_receiver_name, "暂无姓名 ${item?.receiver_phone}")
        } else {
            helper.setText(R.id.tv_receiver_name, "${item?.real_name} ${item?.receiver_phone}")
        }
        if (item?.default_address.isNullOrEmpty() && item?.village.isNullOrEmpty()) {
            helper.setText(R.id.tv_receiver_address, "暂无地址")
        } else {
            if (item?.default_address.isNullOrEmpty()) {
                helper.setText(R.id.tv_receiver_address, "${item?.village}")
            } else {
                helper.setText(R.id.tv_receiver_address, "${item?.default_address}")
            }
        }

        Glide.with(mContext).load(item?.com_logo)
            .apply(RequestOptions().placeholder(R.drawable.ic_avatar_holder))
            .into(helper.itemView.findViewById(R.id.iv_com_logo))
    }
}