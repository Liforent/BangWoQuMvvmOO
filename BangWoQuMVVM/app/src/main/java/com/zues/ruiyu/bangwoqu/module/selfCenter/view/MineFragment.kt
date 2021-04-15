package com.zues.ruiyu.bangwoqu.module.selfCenter.view

import android.content.Intent
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleFragment
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.eventbus.BangwqMessageEvent
import com.zues.ruiyu.bangwoqu.base.https.onResponse
import com.zues.ruiyu.bangwoqu.base.loadsirCallback.ErrorCallBack
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssSPHelper
import com.zues.ruiyu.bangwoqu.module.account.view.MyInfoActivity
import com.zues.ruiyu.bangwoqu.module.home.view.SearchPackageActivity
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.UserInfoResponse
import com.zues.ruiyu.bangwoqu.module.selfCenter.MineViewModel
import com.zues.ruiyu.bangwoqu.module.setting.view.SettingPswActivity
import kotlinx.android.synthetic.main.fragment_mine.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *@Author liforent
 *@create 2020/9/8 10:11
 */
class MineFragment : BaseLifeCycleFragment<MineViewModel>() {
    var balance: String? = ""
    var alipay: String? = ""
    override fun initData(): Boolean {
        getUserInfo()
        return true
    }

    private val userId by lazy {
        ZssSPHelper.getInstance(requireContext())
            .getSharedPreference(Constant.SP_KEY_USER_ID, 0L) as Long
    }


    override fun initDataObserver() {
        mViewModel.mUserInfoData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                updateUserInfo(it.data)
                fillUi(it.data)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getUserInfo()
    }

    override fun showSystemErrorMsg(msg: String) {
        super.showSystemErrorMsg(msg)
        loadService.showCallback(ErrorCallBack::class.java)
    }


    override fun reLoad() {
        super.reLoad()
        getUserInfo()
    }


    private fun fillUi(userInfoResponse: UserInfoResponse?) {
        balance = userInfoResponse?.balance
        alipay = userInfoResponse?.alipay
        tv_money.text = balance?.toDouble().toString()
        tv_nickname.text = userInfoResponse?.real_name
        tv_phone.text = userInfoResponse?.phone
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_mine
    }

    public fun getUserInfo() {
        mViewModel.getUserInfo(Constant.TOKEN, userId)
    }

    override fun initView() {
        super.initView()
        title_view.setLeftImgVisible(false)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: Any?) {
        if (event is BangwqMessageEvent) {
            when (event.message) {
                Constant.EVENT_UPDATE_HOME_SET_PSW -> {
                }
                Constant.EVENT_UPDATE_MINE_USERINFO -> {
                    getUserInfo()

                }

            }

        }
    }

    override fun initEvent() {
        tv_withdraw.setOnClickListener {
            val intent = Intent(requireContext(), WithdrawActivity::class.java)
            intent.putExtra(Constant.DATA_BALANCE, balance)
            intent.putExtra(Constant.DATA_ALIPAY, alipay)
            intent.putExtra(Constant.DATA_USER_ID, userId)
            startActivity(intent)
        }
        tv_detail.setOnClickListener {
            startActivity(Intent(requireContext(), FinancialRecordsActivity::class.java))
        }
        tv_set_psw.setOnClickListener {
            startActivity(Intent(requireContext(), SettingPswActivity::class.java))
        }
        iv_setting.setOnClickListener {
            val intent = Intent(requireContext(), MyInfoActivity::class.java)
            intent.putExtra(Constant.DATA_USER_INFO, mUserInfo)
            startActivity(intent)
        }
        tv_manu.setOnClickListener {
            startActivity(Intent(requireActivity(), SearchPackageActivity::class.java))
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }
}