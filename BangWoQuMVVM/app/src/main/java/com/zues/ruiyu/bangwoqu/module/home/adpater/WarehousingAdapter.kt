package com.zues.ruiyu.bangwoqu.module.home.adpater

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.dp2px
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.gone
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.show
import com.zues.ruiyu.bangwoqu.module.home.data.PackageInfoResponse
import org.jetbrains.anko.textColor

/**
 *@Author liforent
 *@create 2020/9/14 5:42
 */
class WarehousingAdapter :
    BaseQuickAdapter<PackageInfoResponse.PackageInfo, BaseViewHolder>(
        R.layout.item_warehousing_detail,
        null
    ) {
    override fun convert(helper: BaseViewHolder, item: PackageInfoResponse.PackageInfo?) {
        helper.setText(R.id.tv_date, item?.time?.substring(5, 10))
            .setText(R.id.tv_time, item?.time?.substring(11, 16))
            .setText(R.id.tv_status, item?.status)
            .setText(R.id.tv_detail_content, item?.context)

        if (helper.layoutPosition == 0) {
            helper.itemView.findViewById<TextView>(R.id.tv_date).apply {
                textSize = 15f
                textColor = Color.parseColor("#333333")
            }
            helper.itemView.findViewById<TextView>(R.id.tv_status).apply {
                textColor = Color.parseColor("#333333")

            }
            helper.itemView.findViewById<TextView>(R.id.tv_time).apply {
                textColor = Color.parseColor("#333333")
            }
            helper.itemView.findViewById<TextView>(R.id.tv_detail_content).apply {
                textColor = Color.parseColor("#333333")
            }
            helper.itemView.findViewById<ImageView>(R.id.iv_checked).show()
            helper.itemView.findViewById<ImageView>(R.id.iv_dot).gone()
            val view = helper.itemView.findViewById<View>(R.id.view_line0)
            val lp = ConstraintLayout.LayoutParams(1.dp2px(view.context),0)
            lp.setMargins(12.dp2px(view.context),25.dp2px(view.context),0,0)
            lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            lp.startToEnd = R.id.tv_time

            view.layoutParams = lp
        } else {
            helper.itemView.findViewById<ImageView>(R.id.iv_checked).gone()

        }


    }
}
