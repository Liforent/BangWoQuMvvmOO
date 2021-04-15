package com.zues.ruiyu.bangwoqu.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gyf.immersionbar.ImmersionBar
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import org.greenrobot.eventbus.EventBus

/**
 *@Author liforent
 *@create 2020/8/24 14:27
 */
abstract class BaseFragment : Fragment() {

    protected open fun getTopLayoutId(): Int = 0
    lateinit var loadService: LoadService<*>
    val mContext by lazy {
        requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(getLayoutId(), null)
        loadService = LoadSir.getDefault().register(rootView) { reLoad() }
        EventBus.getDefault().register(this)
        return loadService.loadLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ImmersionBar.with(this).init()
        initView()
        initEvent()
    }

    abstract fun initView()

    open fun initEvent() {}

    abstract fun getLayoutId(): Int

    // 重新加载
    open fun reLoad() {

    }

}