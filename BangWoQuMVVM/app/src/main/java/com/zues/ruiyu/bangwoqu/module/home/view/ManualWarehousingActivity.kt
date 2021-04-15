package com.zues.ruiyu.bangwoqu.module.home.view

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleActivity
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.eventbus.BangwqMessageEvent
import com.zues.ruiyu.bangwoqu.base.https.onResponse
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssDeviceHelper
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssSPHelper
import com.zues.ruiyu.bangwoqu.module.activity.MainActivity
import com.zues.ruiyu.bangwoqu.module.home.adpater.WarehousingAdapter
import com.zues.ruiyu.bangwoqu.module.home.data.CompanyInfoResponse
import com.zues.ruiyu.bangwoqu.module.home.HomeViewModel
import com.zues.ruiyu.bangwoqu.module.home.data.PackageInfoResponse
import kotlinx.android.synthetic.main.activity_manual_warehousing.*
import kotlinx.android.synthetic.main.activity_manual_warehousing.title_view
import kotlinx.android.synthetic.main.activity_manual_warehousing.tv_tracking_number
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast

/**
 *@Author liforent
 *@create 2020/9/10 18:29
 */
class ManualWarehousingActivity : BaseLifeCycleActivity<HomeViewModel>() {

    var fee: Double = 1.0
    val mAdapter by lazy {
        WarehousingAdapter()
    }

    override fun initData(): Boolean = false

    private val packageStatusResponse by lazy {
        intent.getSerializableExtra(Constant.DATA_PACKAGE_INFO) as PackageInfoResponse
    }
    val phone by lazy {
        intent.getSerializableExtra(Constant.DATA_USER_PHONE) as String
    }

    val user_id by lazy {
        ZssSPHelper.getInstance(this)
            .getSharedPreference(Constant.SP_KEY_USER_ID, 0L) as Long
    }

    override fun initDataObserver() {
        mViewModel.mWarehousingPackageData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                toast("入库成功！")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                EventBus.getDefault().post(BangwqMessageEvent(Constant.EVENT_UPDATE_HOME))
            }
        })
    }

    override fun getLayoutId(): Int = R.layout.activity_manual_warehousing

    override fun initView() {
        super.initView()
        initPackageInfo()
        initFee()
        initRecy()
    }

    @SuppressLint("SetTextI18n")
    private fun initRecy() {
        tv_detail_title.text = "${packageStatusResponse.com_name} ${packageStatusResponse.tracking_number}"
        recy_detail_content.adapter = mAdapter
        recy_detail_content.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mAdapter.setNewData(packageStatusResponse.info)
    }


    @SuppressLint("SetTextI18n")
    private fun initPackageInfo() {

        Glide.with(this).load(packageStatusResponse.com_logo).into(iv_avatar)
        tv_com.text = packageStatusResponse.com_name + "单号"
        tv_tracking_number.text = packageStatusResponse.tracking_number
        et_name.setText(phone)
    }

    private fun initFee() {
        tv_price_1.isSelected = true
        et_input_price.visibility = View.GONE
        view_line2.visibility = View.INVISIBLE
        tv_price_1.setOnClickListener {
            tv_price_1.isSelected = true
            tv_price_2.isSelected = false
            tv_price_3.isSelected = false
            tv_price_4.isSelected = false
            tv_price_5.isSelected = false
            fee = 1.0
            view_line2.visibility = View.INVISIBLE
            et_input_price.visibility = View.GONE

        }
        tv_price_2.setOnClickListener {
            tv_price_1.isSelected = false
            tv_price_2.isSelected = true
            tv_price_3.isSelected = false
            tv_price_4.isSelected = false
            tv_price_5.isSelected = false
            fee = 2.0
            view_line2.visibility = View.INVISIBLE

            et_input_price.visibility = View.GONE

        }
        tv_price_3.setOnClickListener {
            tv_price_1.isSelected = false
            tv_price_2.isSelected = false
            tv_price_3.isSelected = true
            tv_price_4.isSelected = false
            tv_price_5.isSelected = false
            fee = 3.0
            view_line2.visibility = View.INVISIBLE

            et_input_price.visibility = View.GONE

        }
        tv_price_4.setOnClickListener {
            tv_price_1.isSelected = false
            tv_price_2.isSelected = false
            tv_price_3.isSelected = false
            tv_price_4.isSelected = true
            tv_price_5.isSelected = false
            fee = 5.0
            view_line2.visibility = View.INVISIBLE

            et_input_price.visibility = View.GONE

        }
        tv_price_5.setOnClickListener {
            tv_price_1.isSelected = false
            tv_price_2.isSelected = false
            tv_price_3.isSelected = false
            tv_price_4.isSelected = false
            tv_price_5.isSelected = true
            view_line2.visibility = View.VISIBLE

            et_input_price.visibility = View.VISIBLE
        }
    }

    override fun initEvent() {
        iv_copy.setOnClickListener {
            toast("复制成功")
            ZssDeviceHelper.copyToClipboard(this, packageStatusResponse.tracking_number)
        }
        tv_submit.setOnClickListener {
            if (tv_price_5.isSelected) {
                if ("" == et_input_price.text.toString()) {
                    toast("请输入代取价格")
                } else {
                    fee = et_input_price.text.toString().toDouble()
                }
            }
            showLoadingView()
            mViewModel.warehousingPackage(
                et_name.text.toString(),
                tv_tracking_number.text.toString(),
                packageStatusResponse.com,
                packageStatusResponse.info,
                user_id,
                fee.toFloat()
            )

        }
        title_view.setBack {
            finish()
        }
    }


}