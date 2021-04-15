package com.zues.ruiyu.bangwoqu.module.home.adpater

import android.graphics.Color
import android.widget.TextView
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.module.home.data.PackagesListResponse
import com.zues.ruiyu.bangwoqu.module.home.view.HomeFragment
import org.jetbrains.anko.backgroundColor

/**
 *@Author liforent
 *@create 2020/9/14 5:42
 */
class HomeAdapter(val fragment: HomeFragment) :
    BaseItemDraggableAdapter<PackagesListResponse, BaseViewHolder>(
        R.layout.item_home,
        null
    ) {


    override fun convert(helper: BaseViewHolder, item: PackagesListResponse?) {
        val time = item?.create_time?.substring(5, 19)?.split("T")

        helper.setText(R.id.tv_address, item?.receiver_address?.substring(0, 3))
            .setText(R.id.tv_time, time?.get(0) + "\n" + time?.get(1)?.substring(0, 5))
            .setText(R.id.tv_receiver_name, item?.real_name)
            .setText(R.id.tv_tracking_number, item?.tracking_number)
            .addOnClickListener(R.id.tv_delete)
        when (item?.status) {
            Constant.PACKAGE_STATUS_ATTEMPT_REPLACE -> {
                helper.itemView.findViewById<TextView>(R.id.tv_status).apply {
                    text = "代取中"
                    //setTextColor(Color.parseColor("#FFDF0324"))
                }
            }
            Constant.PACKAGE_STATUS_DELIVERING -> helper.setText(R.id.tv_status, "配送")
            Constant.PACKAGE_STATUS_DONE -> {
                if(item.is_self == Constant.IS_SELF){
                    helper.setText(R.id.tv_status, "自取")
                }else{
                    helper.setText(R.id.tv_status, "完成")
                }

            }
            Constant.PACKAGE_STATUS_GET_ORDER -> helper.setText(R.id.tv_status, "接单")
            Constant.PACKAGE_STATUS_WAREHOUSE -> helper.setText(R.id.tv_status, "入库")
            Constant.PACKAGE_STATUS_GET_PACKAGE -> helper.setText(R.id.tv_status, "取件")
        }

        val tvDelete = helper.itemView.findViewById<TextView>(R.id.tv_delete)
        val swipeMenuLayout = helper.itemView.findViewById<SwipeMenuLayout>(R.id.swipe_layout)
        swipeMenuLayout.setIos(false).isLeftSwipe = true
        swipeMenuLayout.setOnSwipeListener(object : SwipeMenuLayout.OnSwipeListener {
            override fun onSmoothExpand() {
            }

            override fun onSmoothClosed() {
                fragment.isDelete = true
            }
        })
        tvDelete.apply {
            backgroundColor = if (item?.status == Constant.PACKAGE_STATUS_WAREHOUSE) {
                 Color.parseColor("#ea5353")
            } else {
               Color.parseColor("#999999")
            }
        }


    }
}
