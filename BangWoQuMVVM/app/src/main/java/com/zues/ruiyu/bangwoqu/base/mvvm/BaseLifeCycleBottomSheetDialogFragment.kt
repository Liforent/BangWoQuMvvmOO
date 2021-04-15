package com.zues.ruiyu.bangwoqu.base.mvvm

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kingja.loadsir.callback.SuccessCallback
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.BaseApplication
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.base.commonUtils.ClassUtil
import com.zues.ruiyu.bangwoqu.base.loadsirCallback.EmptyCallBack
import com.zues.ruiyu.bangwoqu.base.loadsirCallback.LoadingCallBack
import com.zues.ruiyu.bangwoqu.module.account.view.LoginActivity
import org.jetbrains.anko.contentView
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast

/**
 *@Author liforent
 *@create 2020/10/11 8:38
 * 多为弹窗，不绑定loadsir了。
 */
abstract class BaseLifeCycleBottomSheetDialogFragment<VM : BaseViewModel<*>> :
    BottomSheetDialogFragment(){
    protected lateinit var mViewModel: VM
    private lateinit var loadingView: PopupWindow
    var isLoading = true
    private var isShowLoadingView = false

//    private val popView by lazy {
//        LayoutInflater.from(this).inflate(R.layout.df_progress_view, null)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyTransDialog)
    }
    abstract fun getLayoutId(): Int

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mViewModel = defaultViewModelProviderFactory.create(ClassUtil.getClass(this))
        return super.onCreateDialog(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate( getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.loadState.observe(viewLifecycleOwner, observer)
        initDataObserver()
        initView(view)
    }


    override fun onStart() {
        super.onStart()
        val window = dialog!!.window
        val lp = window!!.attributes
        lp.dimAmount = 0f
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window.attributes = lp
    }

    open fun initView(view: View) {}

    protected open fun initDataObserver() {}

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
//                        contentView?.postDelayed({
//                            if (isLoading && isShowLoadingView) {
//                                showLoadingPop()
//                            }
//                        }, 500)
                    }

                    StateType.EMPTY -> {
                        closeLoadingPop()
                        showEmptyCallback()
                    }
                    StateType.RE_LOGIN -> {
                        closeLoadingPop()
                        //startActivity(Intent(this, LoginActivity::class.java))
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
//        contentView?.postDelayed({
//            loadingView.dismiss()
//        }, 250)
    }

    public fun showLoadingView() {
        isShowLoadingView = true
    }


    private fun showLoadingPop() {
        //loadingView.showAtLocation(contentView, Gravity.CENTER, 0, 0)
    }


    open fun showEmptyCallback() {
        ZLog.d("showEmptyCallback")
    }

    open fun showSuccessCallback() {
        ZLog.d("showSuccessCallback")
    }

    open fun showLoadingCallback() {
        ZLog.d("showLoadingCallback")
    }

    open fun showErrorMsg(msg: String) {
        toast(msg)
        ZLog.e("showErrorMsg$msg")
    }

    open fun showSystemErrorMsg(msg: String) {
        toast(msg)

        ZLog.e("showSystemErrorMsg$msg")

    }

}