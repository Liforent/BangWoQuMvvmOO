package com.zues.ruiyu.bangwoqu.module.home.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.BaseApplication
import com.zues.ruiyu.bangwoqu.base.commonUtils.ClickUtil
import com.zues.ruiyu.bangwoqu.base.https.ApiList
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleFragment
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.eventbus.BangwqMessageEvent
import com.zues.ruiyu.bangwoqu.base.eventbus.MDWebViewActivity
import com.zues.ruiyu.bangwoqu.base.https.BasePagingResponse
import com.zues.ruiyu.bangwoqu.base.https.getApiCode
import com.zues.ruiyu.bangwoqu.base.https.onResponse
import com.zues.ruiyu.bangwoqu.base.loadsirCallback.ErrorCallBack
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssSPHelper
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.dp2px
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.getExtHeight
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.gone
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.show
import com.zues.ruiyu.bangwoqu.module.home.adpater.HomeAdapter
import com.zues.ruiyu.bangwoqu.module.home.data.CompanyInfoResponse
import com.zues.ruiyu.bangwoqu.module.home.data.PackagesListResponse
import com.zues.ruiyu.bangwoqu.module.home.HomeViewModel
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.UserInfoResponse
import com.zues.ruiyu.bangwoqu.module.setting.view.SettingPswActivity
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.recy_content
import kotlinx.android.synthetic.main.fragment_home.rl_refresh
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.displayMetrics

/**
 *@Author liforent
 *@create 2020/9/8 9:53
 */

class HomeFragment : BaseLifeCycleFragment<HomeViewModel>() {

    private var isRefresh: Boolean = true
    private var isNoMore: Boolean = true
    private var mPage: Int = 1
    private var curPos = 0


    var isDelete = true
    private val mAdapter by lazy {
        HomeAdapter(this)
    }

    override fun initData(): Boolean {
        refreshPage()
        getUserInfo()
        return true
    }

    override fun showEmpty() {
        showSuccessCallback()
        if (isRefresh) {
            rl_refresh?.finishRefresh()
        }
    }

    override fun showSystemErrorMsg(msg: String) {
        super.showSystemErrorMsg(msg)
        if (isRefresh) {
            rl_refresh?.finishRefresh(1000)
        } else {
            rl_refresh?.finishLoadMore(1000)
        }
        if (msg.getApiCode() == ApiList.LOAD_PACKAGES.toString()) {
            loadService.showCallback(ErrorCallBack::class.java)
        }

    }

    private fun getUserInfo() {
        mViewModel.getUserInfo(
            Constant.TOKEN,
            ZssSPHelper.getInstance(requireActivity())
                .getSharedPreference(Constant.SP_KEY_USER_ID, 0L) as Long
        )

    }

    override fun initDataObserver() {

        mViewModel.mLoadPackagesData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                isNoMore = it.total <= it.page_num * it.page_size
                fillUi(it)
            }
        })

        mViewModel.mUserInfoData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                initSettingPswTips(it.data)
                updateUserInfo(it.data)
            }
        })
        mViewModel.mRemovePackageInfoData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                mAdapter.remove(curPos)
                isDelete = true
                curPos = 0
                Toast.makeText(requireContext(), "删除成功！", Toast.LENGTH_SHORT).show()
            }
        }
        )
    }


    override fun reLoad() {
        super.reLoad()
        initData()
    }

    @SuppressLint("InflateParams")
    private fun fillUi(basePagingResponse: BasePagingResponse<List<PackagesListResponse>>?) {
        if (isRefresh) {
            mAdapter.setNewData(basePagingResponse?.data)
            rl_refresh?.finishRefresh(1000)
        } else {
            basePagingResponse?.data?.let { mAdapter.addData(it) }
            rl_refresh?.finishLoadMore(1000)
        }
        if (isNoMore) {
            rl_refresh?.setEnableLoadMore(false)
            val footView = LayoutInflater.from(mContext).inflate(R.layout.layout_footer_view, null)
            if (mAdapter.itemCount > 10) {
                mAdapter.removeAllFooterView()
                mAdapter.addFooterView(footView)
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }


    override fun initView() {
        super.initView()
        initBanner()
        initRecy()
    }

    private fun initRecy() {
        recy_content.adapter = mAdapter
        recy_content.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        //mAdapter.setEmptyView(R.layout.layout_empty_home, recy_content)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            val model = adapter.getItem(position) as PackagesListResponse
            val tvDelete = view.findViewById<TextView>(R.id.tv_delete)
            if (view.id == R.id.tv_delete) {
                if (model.status == Constant.PACKAGE_STATUS_WAREHOUSE) {
                    if (isDelete) {
                        tvDelete.text = "确认删除"
                        // tvDelete.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT)
                        // val lp = ViewGroup.LayoutParams()
                        isDelete = false
                    } else {
                        curPos = position
                        showLoadingView()
                        mViewModel.deletePackage(Constant.TOKEN, model.id)
                    }
                } else {
                    Toast.makeText(requireContext(), "此快递已被派件，无法删除", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initBanner() {
        val mBannerWidth =
            mContext.displayMetrics.widthPixels - 10.dp2px(mContext) * 2
        val mBannerHeight =
            iv_novice_tutorial.getExtHeight(355, 100, mContext, mBannerWidth)
        val lp = RelativeLayout.LayoutParams(mBannerWidth, mBannerHeight)
        val margin = 10.dp2px(mContext)
        lp.setMargins(margin, margin, margin, margin)
        lp.addRule(RelativeLayout.CENTER_IN_PARENT)
        iv_novice_tutorial.layoutParams = lp
    }

    private fun getData(page_num: Int) {
        mViewModel.loadPackages(
            Constant.TOKEN,
            page_num,
            null,
            null,
            (ZssSPHelper.getInstance(requireContext())
                .getSharedPreference(Constant.SP_KEY_USER_ID, 0L) as Long).toString(),
            null,
            null
        )
    }

    override fun initEvent() {
        tv_scan_in.setOnClickListener {
            if (!ClickUtil.isDoubleClick()) {

                startActivity(Intent(requireContext(), ScanInActivity::class.java))
            }
        }
        tv_scan_out.setOnClickListener {
            if (!ClickUtil.isDoubleClick()) {
                startActivity(Intent(requireActivity(), ScannerOutActivity::class.java))
            }
        }
        rl_setting_psw.setOnClickListener {
            startActivity(Intent(requireActivity(), SettingPswActivity::class.java))
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        iv_novice_tutorial.setOnClickListener {

//            val scannerIntent = Intent(requireActivity(), TestScannerActivity::class.java)
//            startActivity(scannerIntent)

            val intent = Intent(requireActivity(), MDWebViewActivity::class.java)
            intent.putExtra("URL", "https://bwqh5.mobiw.com/driver")
            startActivity(intent)
        }

        rl_refresh.setOnRefreshListener {
            mAdapter.removeAllFooterView()
            showLoadingView()
            refreshPage()
            rl_refresh.setEnableLoadMore(true)
        }
        rl_refresh.setOnLoadMoreListener {
            isRefresh = false
            mPage += 1
            getData(mPage)
        }

        ll_search.setOnClickListener {
            startActivity(Intent(requireActivity(), SearchPackageInfoActivity::class.java))
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: Any?) {
        if (event is BangwqMessageEvent) {
            if (Constant.EVENT_UPDATE_HOME == event.message) {
                refreshPage()
            }
            if (Constant.EVENT_UPDATE_HOME_SET_PSW == event.message) {
                initSettingPswTips((requireActivity().application as BaseApplication).getCurUserInfo())
            }
        }
    }

    private fun initSettingPswTips(info: UserInfoResponse?) {
        info?.password?.let {
            if (it) rl_setting_psw?.gone() else rl_setting_psw?.show()
        }


    }

    private fun refreshPage() {
        isRefresh = true
        mPage = 1
        getData(mPage)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

}