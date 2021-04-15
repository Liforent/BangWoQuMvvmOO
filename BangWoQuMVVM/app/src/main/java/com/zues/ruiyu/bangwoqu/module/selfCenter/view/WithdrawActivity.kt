package com.zues.ruiyu.bangwoqu.module.selfCenter.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleActivity
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.eventbus.BangwqMessageEvent
import com.zues.ruiyu.bangwoqu.base.https.onResponse
import com.zues.ruiyu.bangwoqu.module.selfCenter.MineViewModel
import kotlinx.android.synthetic.main.activity_withdraw.*
import kotlinx.android.synthetic.main.activity_withdraw.title_view
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast


/**
 *@Author liforent
 *@create 2020/9/9 16:34
 */
class WithdrawActivity : BaseLifeCycleActivity<MineViewModel>() {
    override fun initData() = false

    override fun initDataObserver() {
        mViewModel.mWithdrawData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                toast("已接受处理")
                postUpdateMessage()
                finish()
            }
        })
    }

    override fun getLayoutId(): Int = R.layout.activity_withdraw


    @SuppressLint("SetTextI18n")
    override fun initView() {
        super.initView()
        tv_valid_amount.text = "可提现金额${mUserInfo?.balance}元"
        tv_withdraw.setBackgroundResource(R.drawable.bg_login_btn_invalid)
        if (TextUtils.isEmpty(mUserInfo?.alipay)) {
            tv_alipay.text = "请绑定支付宝账号"
            tv_alipay.setTextColor(Color.parseColor("#999999"))

        } else {
            tv_alipay.text = mUserInfo?.alipay
            tv_alipay.setTextColor(Color.parseColor("#333333"))
        }
        //et_amount.setFilters(arrayOf<InputFilter>(InputFilterMinMax(0, balance.toDouble().toInt())))
        et_amount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(s)) {
                    val mBalance = mUserInfo?.balance.toString().toDouble().toInt()
                    if (s.toString().toDouble().toInt() > mBalance) {
                        et_amount.setText(mBalance.toString())
                        et_amount.setSelection(mBalance.toString().length)//将光标移至文字末尾
                    }
                    tv_withdraw.isClickable = true
                    tv_withdraw.setBackgroundResource(R.drawable.bg_login)
                } else {
                    tv_withdraw.isClickable = false
                    tv_withdraw.setBackgroundResource(R.drawable.bg_login_btn_invalid)
                }
            }

            override fun afterTextChanged(s: Editable?) {


            }
        })
        EventBus.getDefault().let {
            if (!it.isRegistered(this)) {
                it.register(this)
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: Any?) {
        if (event is BangwqMessageEvent) {
            when (event.message) {
                Constant.EVENT_UPDATE_MINE_ALIPAY -> {
                    tv_alipay.text = mUserInfo?.alipay
                    tv_alipay.setTextColor(Color.parseColor("#333333"))
                }
            }

        }
    }

    override fun initEvent() {
        cl_bind_alipay.setOnClickListener {
            val intent = Intent(this, BindAlipayActivity::class.java)
            startActivity(intent)
        }
        tv_withdraw_all.setOnClickListener {
            et_amount.setText(mUserInfo?.balance)
            et_amount.setSelection(et_amount.text.length)//将光标移至文字末尾
            tv_withdraw.setBackgroundResource(R.drawable.bg_login)
        }
        tv_withdraw.setOnClickListener {
            showLoadingView()
            mViewModel.withDraw(et_amount.text.toString().toDouble(), Constant.WITHDRAW_TYPE_ALIPAY)
        }
        tv_withdraw.isClickable = false

        title_view.setBack {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().let {
            if (it.isRegistered(this)) {
                it.unregister(this)
            }
        }
    }

    private fun postUpdateMessage() {
        EventBus.getDefault().post(BangwqMessageEvent(Constant.EVENT_UPDATE_MINE_USERINFO))
    }



}