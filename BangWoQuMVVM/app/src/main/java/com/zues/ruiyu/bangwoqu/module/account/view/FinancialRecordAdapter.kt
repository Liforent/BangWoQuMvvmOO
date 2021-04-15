package com.zues.ruiyu.bangwoqu.module.account.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.FinancialRecordsResponse
import org.jetbrains.anko.textColor

/**
 *@Author liforent
 *@create 2020/9/14 5:42
 */
class FinancialRecordAdapter :
    BaseQuickAdapter<FinancialRecordsResponse, BaseViewHolder>(
        R.layout.item_financial_record,
        null
    ) {
    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: FinancialRecordsResponse?) {
        helper.setText(R.id.tv_type, item?.remark)
            .setText(R.id.tv_time, item?.create_time?.substring(0, 19)?.replace("T", " "))
            .setText(R.id.tv_num, item?.amount)

        when (item?.type) {
            0 -> {
                helper.itemView.findViewById<TextView>(R.id.tv_type).textColor =
                    Color.parseColor("#FFDF503C")


            }
            1 -> {
                helper.itemView.findViewById<TextView>(R.id.tv_type).textColor =
                    Color.parseColor("#333333")
                helper.itemView.findViewById<TextView>(R.id.tv_num).text = "+${item.amount}"
            }
        }

    }
}
