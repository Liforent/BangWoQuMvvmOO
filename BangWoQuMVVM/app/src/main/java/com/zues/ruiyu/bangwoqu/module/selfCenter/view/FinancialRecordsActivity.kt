package com.zues.ruiyu.bangwoqu.module.selfCenter.view

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleActivity
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.Constant.FIRST_PAGE
import com.zues.ruiyu.bangwoqu.base.https.BasePagingResponse
import com.zues.ruiyu.bangwoqu.base.https.onResponse
import com.zues.ruiyu.bangwoqu.base.loadsirCallback.ErrorCallBack
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssSPHelper
import com.zues.ruiyu.bangwoqu.module.account.view.FinancialRecordAdapter
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.FinancialRecordsResponse
import com.zues.ruiyu.bangwoqu.module.selfCenter.MineViewModel
import kotlinx.android.synthetic.main.activity_account_detail.recy_content
import kotlinx.android.synthetic.main.activity_account_detail.rl_refresh
import kotlinx.android.synthetic.main.activity_account_detail.title_view

/**
 * 账户明细
 *@Author liforent
 *@create 2020/9/10 15:19
 */
class FinancialRecordsActivity : BaseLifeCycleActivity<MineViewModel>() {
    lateinit var mTmpLoadSir: LoadService<Any>
    private var isRefresh: Boolean = true
    private var isNoMore: Boolean = true
    private var mPage: Int = FIRST_PAGE
    override fun initData(): Boolean {
        refreshPage()
        return true
    }

    val mAdapter = FinancialRecordAdapter()
    private val userId by lazy {
        ZssSPHelper.getInstance(this).getSharedPreference(Constant.SP_KEY_USER_ID, 0L) as Long
    }

    override fun initLoadSir() {
        mTmpLoadSir = LoadSir.getDefault().register(rl_refresh) {
            showLoadingCallback()
            refreshPage()
        }
        mSlectedLoadservice = mTmpLoadSir
    }

    override fun getLayoutId(): Int = R.layout.activity_account_detail

    override fun reLoad() {
        super.reLoad()
        refreshPage()
    }

    private fun refreshPage() {
        isRefresh = true
        mPage = FIRST_PAGE
        getData(mPage)
    }


    override fun initView() {
        super.initView()
        recy_content.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(this@FinancialRecordsActivity, RecyclerView.VERTICAL, false)
        }
        title_view.setBack {
            finish()
        }
    }

    override fun initDataObserver() {
        mViewModel.mFinancialRecordsData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                isNoMore = it.total <= it.page_num * it.page_size
                fillUi(it)
            }
        })
    }

    override fun showSystemErrorMsg(msg: String) {
        super.showSystemErrorMsg(msg)
        if (isRefresh) {
            rl_refresh?.finishRefresh(1000)
        } else {
            rl_refresh?.finishLoadMore(1000)
        }
        mSlectedLoadservice.showCallback(ErrorCallBack::class.java)

    }

    // TODO: 2020/11/13 该页面的数据列表展示需要再次测试
    private fun fillUi(basePagingResponse: BasePagingResponse<List<FinancialRecordsResponse>>) {
        if (isRefresh) {
            mAdapter.setNewData(basePagingResponse.data)
            rl_refresh?.finishRefresh(1000)

        } else {
            rl_refresh?.finishLoadMore(1000)
            basePagingResponse.data.let { mAdapter.addData(it) }
        }

        if (isNoMore) {
            rl_refresh?.setEnableLoadMore(false)
        }
    }


    override fun initEvent() {

        title_view.setBack {
            finish()
        }

        rl_refresh.setOnRefreshListener {
            refreshPage()
        }
        rl_refresh.setOnLoadMoreListener {
            isRefresh = false
            mPage += 1
            getData(mPage)
        }


    }

    private fun getData(mPage: Int) {
        mViewModel.getFinancialRecords(Constant.TOKEN, mPage, userId, null)
    }


}