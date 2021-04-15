package com.zues.ruiyu.bangwoqu.module.home.view

import android.content.Intent
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleActivity
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.https.onResponse
import com.zues.ruiyu.bangwoqu.base.DialogHelper
import com.zues.ruiyu.bangwoqu.module.home.view.VerifyPhoneDialog.OnConfirmListener
import com.zues.ruiyu.bangwoqu.module.home.HomeViewModel
import kotlinx.android.synthetic.main.activity_search_package.*
import org.jetbrains.anko.toast

/**
 *@Author liforent
 *@create 2020/9/10 19:03
 *
 * 搜索后跳转到入库
 */
class SearchPackageActivity : BaseLifeCycleActivity<HomeViewModel>() {
    private var mPhone: String? = ""
    override fun initData() = false
    override fun initDataObserver() {
        mViewModel.mPackageInfoData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                if (it.data.info != null) {
                    val intent = Intent(this, ManualWarehousingActivity::class.java)
                    intent.putExtra(Constant.DATA_PACKAGE_INFO, it.data)
                    intent.putExtra(Constant.DATA_USER_PHONE, mPhone)
                    startActivity(intent)
                } else {
                    toast("未能查询到相关快递信息！")
                }
            }
        })
    }

    override fun getLayoutId(): Int = R.layout.activity_search_package


    override fun initEvent() {
        title_view.setBack {
            finish()
        }
        iv_clear.setOnClickListener {
            et_tracking_number.setText("")
        }
        iv_search.setOnClickListener {
            if (et_tracking_number.text.toString() == "") {
                toast("请输入快递单号")
            } else {
                //只有顺丰的单子需要手机号码验证
                if (et_tracking_number.text.toString().startsWith("SF")) {
                    DialogHelper.run {
                        showComfirmPhoneDialog(supportFragmentManager, object : OnConfirmListener {
                            override fun onConfirm(phone: String) {
                                mPhone = phone
                                showLoadingView()
                                mViewModel.searchPackage(
                                    et_tracking_number.text.toString(),
                                    null,
                                    phone
                                )
                            }
                        })
                    }
                } else {
                    showLoadingView()
                    mViewModel.searchPackage(
                        et_tracking_number.text.toString(),
                        null,
                        null
                    )
                }

            }
        }

    }
}