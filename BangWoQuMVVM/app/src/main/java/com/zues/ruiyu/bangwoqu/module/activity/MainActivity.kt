package com.zues.ruiyu.bangwoqu.module.activity

import android.util.SparseArray
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.tencent.mmkv.MMKV
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.BaseApplication
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleActivity
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.eventbus.BangwqMessageEvent
import com.zues.ruiyu.bangwoqu.base.https.onResponse
import com.zues.ruiyu.bangwoqu.base.commonUtils.AppManager
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssSPHelper
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.getPhone
import com.zues.ruiyu.bangwoqu.module.home.view.HomeFragment
import com.zues.ruiyu.bangwoqu.module.selfCenter.view.MineFragment
import com.zues.ruiyu.bangwoqu.module.selfCenter.MineViewModel
import com.zues.ruiyu.bangwoqu.ocr.kotlinOcr.MyCameraManager
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast

class MainActivity : BaseLifeCycleActivity<MineViewModel>() {
    private var mExitTime: Long = 0
    override fun initData() = false

    private val mFragmentSparseArray = SparseArray<Fragment>()
    private var mCurrentFragment: Fragment? = null
    var kv = MMKV.defaultMMKV()
    override fun getLayoutId() = R.layout.activity_main

    override fun initView() {
        super.initView()
        Constant.TOKEN =
            ZssSPHelper.getInstance(this).getSharedPreference(Constant.SP_KEY_TOKEN, "") as String

//        Constant.TOKEN = kv.decodeString(Constant.SP_KEY_TOKEN,"")
//        val userid = kv.decodeString(Constant.SP_KEY_USER_ID,"")
//        ZLog.e(Constant.TOKEN + userid)

        initNavView()
        // getUserInfo()
        switchFragment(Constant.TAB_HOME)
        MyCameraManager(this).getCameraID()


    }

    private fun getUserInfo() {
        mViewModel.getUserInfo(
            Constant.TOKEN,
            ZssSPHelper.getInstance(this).getSharedPreference(Constant.SP_KEY_USER_ID, 0L) as Long
        )
    }


    private fun initNavView() {
        nav_view.apply {
            // 取消导航栏图标着色
            itemIconTintList = null
            labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
            setOnNavigationItemSelectedListener { item: MenuItem ->
                if (item.itemId == nav_view.selectedItemId) {
                    return@setOnNavigationItemSelectedListener false
                }
                when (item.itemId) {
                    R.id.navigation_home -> {
                        switchFragment(Constant.TAB_HOME)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.navigation_selfcenter -> {
                        switchFragment(Constant.TAB_MINE)
                        return@setOnNavigationItemSelectedListener true
                    }
                    else -> return@setOnNavigationItemSelectedListener false
                }
            }
        }
    }

    //点击tab无法触发onResume，需要额外手动处理。
    private fun switchFragment(index: Int) {
        val fragmentManager = supportFragmentManager
        val transaction =
            fragmentManager.beginTransaction()
        mCurrentFragment = fragmentManager.findFragmentByTag(index.toString())
        if (mCurrentFragment == null) {
            mCurrentFragment = getFragment(index)
            hideAll(transaction)
            transaction.add(R.id.FramePage, mCurrentFragment!!, index.toString())
        } else {
            hideAll(transaction)
            transaction.show(mCurrentFragment!!)
            if(mCurrentFragment is MineFragment){
                (mCurrentFragment as MineFragment).getUserInfo()
            }
        }

        transaction.commit()
    }

    private fun hideAll(transaction: FragmentTransaction) {
        supportFragmentManager.fragments.forEach {
            transaction.hide(it)
        }
    }


    private fun getFragment(index: Int): Fragment {
        var fragment: Fragment? = mFragmentSparseArray.get(index)
        if (fragment == null) {
            when (index) {
                Constant.TAB_HOME -> fragment = HomeFragment()
                Constant.TAB_MINE -> fragment = MineFragment()
            }
            mFragmentSparseArray.put(index, fragment)
        }
        return fragment!!
    }


    override fun onBackPressed() {
        val time = System.currentTimeMillis()

        if (time - mExitTime > 2000) {
            toast("再次点击退出APP")
            mExitTime = time
        } else {
            AppManager.instance.exitApp(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun event(event: Any?) {
        if (event is BangwqMessageEvent) {
            if (Constant.EVENT_UPDATE_HOME_SET_PSW == event.message) {
                ZLog.e("EVENT_UPDATE_HOME_SET_PSW")
            }
        }
    }

    override fun initDataObserver() {
        mViewModel.mUserInfoData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                (application as BaseApplication).updateUserInfo(it.data)
                ZLog.e(it.data.toString())
            }
        })
    }
}