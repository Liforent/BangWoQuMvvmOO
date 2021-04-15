package com.zues.ruiyu.bangwoqu.base.mvvm

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.zues.ruiyu.bangwoqu.base.commonUtils.ClassUtil
import com.zues.ruiyu.bangwoqu.module.account.view.LoginActivity

/**
 *@Author liforent
 *@create 2020/10/11 8:38
 * 多为弹窗，不绑定loadsir了。
 */
abstract class BaseLifeCycleDialogFragment<VM : BaseViewModel<*>> : BaseHalfScreenDialog() {
    protected lateinit var mViewModel: VM

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mViewModel = defaultViewModelProviderFactory.create(ClassUtil.getClass(this))
        return super.onCreateDialog(savedInstanceState)
    }

    override fun handleViewCreate(view: View) {
        mViewModel.loadState.observe(this, observer)
        initDataObserver()
    }

    protected open fun initDataObserver() {}

    /**
     * 分发应用状态
     */
    private val observer =
        Observer<State> {
            it?.let {
                when (it.code) {
                    StateType.SUCCESS -> showSuccess()
                    StateType.LOADING -> showLoading()
                    StateType.EMPTY -> showEmpty()
                    StateType.RE_LOGIN -> {
                        startActivity(Intent(activity, LoginActivity::class.java))
                    }
                    StateType.ERROR -> {
                        showError(it.msg)
                    }
                    StateType.SYSTEM_ERROR->{
                        showTip(it.msg)
                    }
                }
            }
        }


    open fun showEmpty() {
    }

    open fun showSuccess() {
    }

    open fun showLoading() {
    }

    //默认会显示error callback,可能会造成页面被覆盖，可以通过设置reloadTargetWhileError为false或者重写该方法来避免
    open fun showError(msg: String) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    open fun showTip(msg: String) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

}