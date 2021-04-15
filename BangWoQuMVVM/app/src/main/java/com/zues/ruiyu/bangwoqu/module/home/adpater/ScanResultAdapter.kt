package com.zues.ruiyu.bangwoqu.module.home.adpater

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.module.home.data.BulkStorageModel
import com.zues.ruiyu.bangwoqu.module.home.view.ScanInActivity
import org.jetbrains.anko.textColor


/**
 *@Author liforent
 *@create 2020/9/14 5:42
 */
class ScanResultAdapter(var activity: ScanInActivity) :
    BaseQuickAdapter<BulkStorageModel, BaseViewHolder>(
        R.layout.item_scan_result,
        null
    ) {
    override fun convert(helper: BaseViewHolder, item: BulkStorageModel?) {
        val textViewVillage = helper.itemView.findViewById<TextView>(R.id.tv_village)
        textViewVillage.textColor = Color.parseColor("#333333")
        textViewVillage.isClickable = false
        helper.setText(R.id.tv_tracking_number, item?.tracking_number)
            .setText(R.id.tv_phone_number, item?.receiver_phone)
            .addOnClickListener(R.id.iv_delete)
            .addOnClickListener(R.id.iv_edit)
            .addOnClickListener(R.id.tv_delete)
            .addOnClickListener(R.id.tv_village)
        var strVillage = ""
        if (item?.priceInfo != null) {

            strVillage = if (item.priceInfo!!.name?.isNotEmpty() == true) item.priceInfo!!.name.toString() else "未识别到村"
            helper.setText(R.id.tv_village, strVillage)
                .setText(R.id.tv_freight, "${item.freight}元")
        } else {
            strVillage = "未识别到村"
            helper.setText(R.id.tv_village, strVillage)
                .setText(R.id.tv_freight, "${item?.freight}元")
        }

        if (strVillage == "未识别到村") {
            textViewVillage.textColor = Color.parseColor("#DA3120")
            textViewVillage.isClickable = true
        }

        Glide.with(mContext).load(item?.logo)
            .apply(RequestOptions().placeholder(R.drawable.ic_avatar_holder))
            .into(helper.itemView.findViewById(R.id.iv_icon) as ImageView)

        val tvDelete = helper.itemView.findViewById<TextView>(R.id.tv_delete)
        val swipeMenuLayout = helper.itemView.findViewById<SwipeMenuLayout>(R.id.swipe_layout)
        swipeMenuLayout.setIos(false).isLeftSwipe = true
        swipeMenuLayout.setOnSwipeListener(object : SwipeMenuLayout.OnSwipeListener {
            override fun onSmoothExpand() {

            }

            override fun onSmoothClosed() {
                activity.isDeleteClicked = false
            }
        })
    }
}
