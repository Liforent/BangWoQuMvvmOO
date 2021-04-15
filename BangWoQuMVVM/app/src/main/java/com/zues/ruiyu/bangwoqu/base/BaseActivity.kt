package com.zues.ruiyu.bangwoqu.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.zues.ruiyu.bangwoqu.base.commonUtils.AppManager


abstract class BaseActivity : AppCompatActivity() {
    val mAccountInfo by lazy {
        (application as BaseApplication).accountInfo
    }

    val loadService: LoadService<*> by lazy {
        LoadSir.getDefault().register(this) {
            reLoad()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT// 横屏
        setContentView(getLayoutId())
        ImmersionBar.with(this).init()
        AppManager.instance.addActivity(this)
        initView()
        initEvent()
    }

    open fun initView() {
    }

    open fun initEvent() {

    }

    open fun reLoad() {}

    abstract fun getLayoutId(): Int


    override fun onDestroy() {
        super.onDestroy()
        AppManager.instance.removeActivity(this)
    }

    protected open fun getTopLayoutId(): Int = 0

}