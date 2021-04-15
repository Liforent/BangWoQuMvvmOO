package com.zues.ruiyu.bangwoqu.base.mvvm


import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.lifecycle.Observer

import com.kingja.loadsir.callback.SuccessCallback
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.BaseApplication
import com.zues.ruiyu.bangwoqu.base.BaseFragment
import com.zues.ruiyu.bangwoqu.base.loadsirCallback.EmptyCallBack
import com.zues.ruiyu.bangwoqu.base.loadsirCallback.LoadingCallBack
import com.zues.ruiyu.bangwoqu.base.commonUtils.ClassUtil
import com.zues.ruiyu.bangwoqu.module.account.view.LoginActivity
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.UserInfoResponse

/**
 * Created with Android Studio.
 * Description:
 * @author:
 * @date: 2020/02/22
 * Time: 16:36
 */

abstract class BaseLifeCycleFragment<VM : BaseViewModel<*>> : BaseFragment() {
    protected lateinit var mViewModel: VM
    abstract fun initData(): Boolean
    var mUserInfo: UserInfoResponse? = null
    var isLoading = true
    private var isShowLoadingView = false //是否允许弹出loadingPop,默认不弹。

    private val popView by lazy {
        LayoutInflater.from(activity).inflate(R.layout.df_progress_view, null)
    }
    private lateinit var loadingView: PopupWindow

    override fun initView() {
        //加载中pop,接口响应超过半秒会弹加载中popView,onNext或者onError之后popView会取消。
        initLoadingPop()
        mViewModel = defaultViewModelProviderFactory.create(ClassUtil.getClass(this))
        mViewModel.loadState.observe(this, observer)
        initDataObserver()
        mUserInfo = (requireActivity().application as BaseApplication).getCurUserInfo()
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

    public fun showLoadingView() {
        isShowLoadingView = true
    }

    abstract fun initDataObserver()


    open fun showLoadingCallback() {
        loadService.showCallback(LoadingCallBack::class.java)
    }

    open fun showSuccessCallback() {
        loadService.showCallback(SuccessCallback::class.java)
    }

    open fun showErrorMsg(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        showSuccessCallback()
    }

    //系统错误，默认只toast，如果需要展示ErrorCallback,重写之
    open fun showSystemErrorMsg(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        showSuccessCallback()
    }


    open fun showEmpty() {
        loadService.showCallback(EmptyCallBack::class.java)
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
                        view?.postDelayed({
                            if (isLoading && isShowLoadingView) {
                                showLoadingPop()
                            }
                        }, 500)
                    }

                    StateType.EMPTY -> {
                        closeLoadingPop()
                        showEmpty()
                    }
                    StateType.RE_LOGIN -> {
                        closeLoadingPop()
                        startActivity(Intent(activity, LoginActivity::class.java))
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
        view?.postDelayed({
            loadingView.dismiss()
        }, 250)
    }


    private fun showLoadingPop() {
        loadingView.showAtLocation(view, Gravity.CENTER, 0, 0)

    }

    override fun reLoad() {
        showLoadingCallback()
        super.reLoad()
    }


    fun updateUserInfo(userInfoResponse: UserInfoResponse?) {
        (requireActivity().application as BaseApplication).updateUserInfo(userInfoResponse)
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingView.dismiss()
    }
}