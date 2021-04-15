package com.zues.ruiyu.bangwoqu.base.mvvm

import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.lifecycle.Observer
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.BaseActivity
import com.zues.ruiyu.bangwoqu.base.BaseApplication
import com.zues.ruiyu.bangwoqu.base.loadsirCallback.EmptyCallBack
import com.zues.ruiyu.bangwoqu.base.loadsirCallback.LoadingCallBack
import com.zues.ruiyu.bangwoqu.base.commonUtils.ClassUtil
import com.zues.ruiyu.bangwoqu.module.account.view.LoginActivity
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.UserInfoResponse
import org.jetbrains.anko.contentView
import org.jetbrains.anko.toast

/**
 *@Author liforent
 *@create 2020/8/25 17:32
 */
abstract class BaseLifeCycleActivity<VM : BaseViewModel<*>> : BaseActivity() {
    abstract fun initData(): Boolean
    protected lateinit var mViewModel: VM
    protected lateinit var mSlectedLoadservice: LoadService<*>
    private lateinit var loadingView: PopupWindow
    var isLoading = true
    private var isShowLoadingView = false
    val mUserInfo by lazy {
        (application as BaseApplication).getCurUserInfo()
    }
    private val popView by lazy {
        LayoutInflater.from(this).inflate(R.layout.df_progress_view, null)
    }

    /**
     * 分发应用状态
     */
    private val observer =
        Observer<State> {
            it?.let {
                when (it.code) {
                    StateType.SUCCESS -> {
                        closeLoadingPop()
                        showSuccessCallback()
                    }
                    StateType.LOADING -> {
                        isLoading = true
                        contentView?.postDelayed({
                            if (isLoading && isShowLoadingView) {
                                showLoadingPop()
                            }
                        }, 500)
                    }

                    StateType.EMPTY -> {
                        closeLoadingPop()
                        showEmptyCallback()
                    }
                    StateType.RE_LOGIN -> {
                        closeLoadingPop()
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                    StateType.ERROR -> {
                        closeLoadingPop()
                        showErrorMsg(it.msg)
                    }
                    StateType.SYSTEM_ERROR -> {
                        closeLoadingPop()
                        showSystemErrorMsg(it.msg)
                    }
                }
            }
        }

    private fun closeLoadingPop() {
        isShowLoadingView = false
        isLoading = false
        //如果加载中view弹出来了，允许存在200毫秒，避免一闪而过
        contentView?.postDelayed({
            loadingView.dismiss()
        }, 250)
    }

    public fun showLoadingView() {
        isShowLoadingView = true
    }


    private fun showLoadingPop() {
        loadingView.showAtLocation(contentView, Gravity.CENTER, 0, 0)
    }


    override fun initView() {
        super.initView()
        //加载中pop
        initLoadingPop()
        initLoadSir()
        mViewModel = defaultViewModelProviderFactory.create(ClassUtil.getClass(this))
        mViewModel.loadState.observe(this, observer)
        initDataObserver()
        if (initData()) {
            showLoadingCallback()
        } else {
            showSuccessCallback()
        }
    }

    private fun initLoadingPop() {
        loadingView = PopupWindow(
            popView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        loadingView.setBackgroundDrawable(null)
        loadingView.isOutsideTouchable = false

    }

    //默认为绑定根布局的loadService，如果不需要绑定根布局，需要重写该方法。
    open fun initLoadSir() {
        mSlectedLoadservice = LoadSir.getDefault().register(this) {

            reLoad()
        }
    }

    abstract fun initDataObserver()

    open fun showEmptyCallback() {
        mSlectedLoadservice.showCallback(EmptyCallBack::class.java)
    }

    open fun showSuccessCallback() {
        mSlectedLoadservice.showCallback(SuccessCallback::class.java)
    }

    open fun showLoadingCallback() {
        mSlectedLoadservice.showCallback(LoadingCallBack::class.java)
    }

    open fun showErrorMsg(msg: String) {
        mSlectedLoadservice.showCallback(SuccessCallback::class.java)
        toast(msg)
    }

    open fun showSystemErrorMsg(msg: String) {
        toast(msg)
        mSlectedLoadservice.showSuccess()
    }


    fun updateUserInfo(userInfoResponse: UserInfoResponse?) {
        (application as BaseApplication).updateUserInfo(userInfoResponse)
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingView.dismiss()
    }
}