package com.zues.ruiyu.bangwoqu.module.home.adpater

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.module.home.data.AreaInfoResponse

/**
 *@Author liforent
 *@create 2020/12/11 9:38
 */

class AreaInfoAdapter :
    BaseQuickAdapter<AreaInfoResponse, BaseViewHolder>(R.layout.item_mark_address, null) {
    override fun convert(helper: BaseViewHolder, item: AreaInfoResponse?) {
        helper.setText(R.id.tv_completed_village, item?.name)
    }
}