package com.zues.ruiyu.bangwoqu.module.home.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.gyf.immersionbar.ImmersionBar
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.base.commonUtils.InputMethodUtils
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.gone
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.show
import com.zues.ruiyu.bangwoqu.base.https.onResponse
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleActivity
import com.zues.ruiyu.bangwoqu.module.home.HomeViewModel
import com.zues.ruiyu.bangwoqu.module.home.adpater.SearchPackageInfoAdapter
import com.zues.ruiyu.bangwoqu.module.home.data.PackagesListResponse
import kotlinx.android.synthetic.main.df_search_package_info.*
import kotlinx.android.synthetic.main.df_search_package_info.rl_refresh
import org.jetbrains.anko.toast

/**
 *@Author liforent
 *@create 2020/12/14 15:16
 *
 *
 */
class SearchPackageInfoActivity : BaseLifeCycleActivity<HomeViewModel>() {

    private var mAdapter = SearchPackageInfoAdapter()
    override fun initData(): Boolean = false
    private var mPage = 1
    private var isPhone = true
    private var isNoMore = false
    private var isRefresh = true


    override fun getLayoutId(): Int = R.layout.df_search_package_info

    override fun showEmptyCallback() {
        //super.showEmptyCallback()
        ll_empty.show()
        tv_tips.gone()
        rl_refresh.gone()
        if (isRefresh) {
            rl_refresh?.finishRefresh(1000)
        }
    }

    override fun initDataObserver() {
        mViewModel.mLoadPackagesData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                ll_empty.gone()
                tv_tips.gone()
                rl_refresh.show()
                isNoMore = it.total <= it.page_num * it.page_size
                fillUi(it.data)
            }
        })
    }

    override fun showErrorMsg(msg: String) {
        super.showErrorMsg(msg)
        if (isRefresh) {
            rl_refresh?.finishRefresh(1000)
        } else {
            rl_refresh?.finishLoadMore(1000)
        }
    }


    override fun showSystemErrorMsg(msg: String) {
        super.showSystemErrorMsg(msg)
        if (isRefresh) {
            rl_refresh?.finishRefresh(1000)
        } else {
            rl_refresh?.finishLoadMore(1000)
        }
    }


    override fun initView() {
        super.initView()
        ImmersionBar.with(this)
            .transparentBar()
            .statusBarColor(R.color.white)
            .autoStatusBarDarkModeEnable(true, 0f)
            .init()

        tv_search_phone.setTextColor(Color.parseColor("#3271F6"))
        tv_search_tracking_number.setTextColor(resources.getColor(R.color.black11, null))
        indicator_phone.show()
        indicator_tracking_number.gone()
        isPhone = true
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = mAdapter
    }

    override fun initEvent() {
        super.initEvent()
        tv_cancel.setOnClickListener {
            finish()
        }
        InputMethodUtils.openKeyBoard(this, et_search)
        et_search.setOnEditorActionListener { v, actionId, event ->
            if (et_search.text.toString() == "") {
                toast("请输入单号或手机号")
                false
            } else {
                isRefresh = true
                isNoMore = false
                mAdapter.setEnableLoadMore(true)
                mPage = 1
                mAdapter.removeAllFooterView()
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchPackages(isPhone, mPage)
                    return@setOnEditorActionListener false
                }
                false
            }


        }
        tv_search_phone.setOnClickListener {

            tv_search_phone.setTextColor(Color.parseColor("#3271F6"))
            tv_search_tracking_number.setTextColor(resources.getColor(R.color.black11, null))
            indicator_phone.show()
            indicator_tracking_number.gone()
            isPhone = true
        }
        tv_search_tracking_number.setOnClickListener {

            isPhone = false
            tv_search_phone.setTextColor(resources.getColor(R.color.black11, null))
            tv_search_tracking_number.setTextColor(Color.parseColor("#3271F6"))
            indicator_phone.gone()
            indicator_tracking_number.show()

        }
        mAdapter.setOnItemClickListener { adapter, view, position ->
            InputMethodUtils.closeKeyBoard(this, et_search)
            PackageDetailInfoDialogFragment().apply {
                val bundle = Bundle()
                bundle.putSerializable(
                    Constant.DATA_ITEM,
                    adapter.getItem(position) as PackagesListResponse
                )
                arguments = bundle
            }.show(supportFragmentManager, PackageDetailInfoDialogFragment::class.qualifiedName)
        }
        rl_refresh.setOnLoadMoreListener {
            isRefresh = false
            mPage += 1
            searchPackages(isPhone, mPage)
        }
        rl_refresh.setEnableRefresh(false)
    }


    private fun fillUi(data: List<PackagesListResponse>) {
        if (isRefresh) {
            mAdapter.setNewData(data)
            mAdapter.notifyDataSetChanged()
            rl_refresh?.finishRefresh(1000)
        } else {
            mAdapter.addData(data)
            rl_refresh?.finishLoadMore(1000)
        }
        if (isNoMore) {
            rl_refresh?.setEnableLoadMore(false)
            val footView = LayoutInflater.from(this).inflate(R.layout.layout_footer_view, null)
            if (mAdapter.itemCount > 5) {
                mAdapter.removeAllFooterView()
                mAdapter.addFooterView(footView)
            }
        }

    }


    private fun searchPackages(isPhone: Boolean, pageNum: Int) {
        showLoadingView()
        if (isPhone) {
            mViewModel.loadPackages(
                Constant.TOKEN,
                pageNum,
                et_search.text.toString(),
                null,
                null,
                null,
                null
            )
        } else {
            mViewModel.loadPackages(
                Constant.TOKEN,
                pageNum,
                null,
                et_search.text.toString(),
                null,
                null,
                null
            )
        }

    }

}