package com.zues.ruiyu.bangwoqu.module.account.view

import android.annotation.SuppressLint
import android.content.Intent
import com.example.areaselectorlibrary.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleActivity
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.eventbus.BangwqMessageEvent
import com.zues.ruiyu.bangwoqu.base.https.onResponse

import com.zues.ruiyu.bangwoqu.base.DialogHelper
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssSPHelper
import com.zues.ruiyu.bangwoqu.module.account.AccountViewModel
import com.zues.ruiyu.bangwoqu.module.activity.AppStartActivity
import com.zues.ruiyu.bangwoqu.module.selfCenter.data.UserInfoResponse
import kotlinx.android.synthetic.main.activity_my_info.*
import kotlinx.android.synthetic.main.activity_my_info.title_view
import kotlinx.android.synthetic.main.activity_my_info.et_phone
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.util.ArrayList


/**
 *@Author liforent
 *@create 2020/9/16 16:15
 */
class MyInfoActivity : BaseLifeCycleActivity<AccountViewModel>() {
    override fun initData(): Boolean = false

    override fun initDataObserver() {
        mViewModel.mUserInfoData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                toast("修改成功！")
                updateUserInfo(it.data)
                fillUI(it.data)
            }
        })
    }

    override fun getLayoutId(): Int = R.layout.activity_my_info

    @SuppressLint("SetTextI18n")
    override fun initView() {
        super.initView()
        fillUI(mUserInfo)
    }

    @SuppressLint("SetTextI18n")
    private fun fillUI(userInfo: UserInfoResponse?) {
        tv_name.text = userInfo?.real_name
        tv_phone.text = userInfo?.phone
        et_phone.text = "${userInfo?.province}${userInfo?.city.toString()}${userInfo?.county}${userInfo?.town}"
        tv_detail_address.text = "${userInfo?.default_address}"

    }

    override fun initEvent() {
        title_view.setBack {
            finish()
            postUpdateMessage()
        }
        tv_logout.setOnClickListener {
            clearSp()
            startActivity(Intent(this, AppStartActivity::class.java))
        }

        ll_place.setOnClickListener {
            onAddressPicker()
        }

        ll_user_name.setOnClickListener {
            DialogHelper.showEditDialog(
                supportFragmentManager, "修改昵称", "请输入昵称(不超过8个字符)",
                object : EditInfoDialogFragment.DFOnClickListener {
                    override fun onLeftBtnClicked() {

                    }

                    override fun onRightBtnClicked(etContent: String?) {
                        showLoadingView()
                        mViewModel.updateUserInfo(
                            Constant.TOKEN,
                            mUserInfo?.id!!,
                            etContent,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                        )
                    }

                }
            )


        }
        ll_detail_address.setOnClickListener {
            DialogHelper.showEditDialog(
                supportFragmentManager, "修改详细地址", "请输入详细地址",
                object : EditInfoDialogFragment.DFOnClickListener {
                    override fun onLeftBtnClicked() {

                    }

                    override fun onRightBtnClicked(etContent: String?) {
                        showLoadingView()
                        mViewModel.updateUserInfo(
                            Constant.TOKEN,
                            mUserInfo?.id!!,
                            null,
                            null,
                            null,
                            null,
                            null,
                            etContent,
                            null
                        )
                    }

                }
            )
        }

    }

    private fun postUpdateMessage() {
        EventBus.getDefault().post(BangwqMessageEvent(Constant.EVENT_UPDATE_MINE_USERINFO))
    }

    private fun clearSp() {
        ZssSPHelper.getInstance(this).put(Constant.SP_KEY_USER_ID, 0L)
        ZssSPHelper.getInstance(this).put(Constant.SP_KEY_TOKEN, "")
    }

    fun onAddressPicker() {
        val provinceList: ArrayList<AreaBean> = ArrayList<AreaBean>()
        val cityList: ArrayList<AreaBean> = ArrayList<AreaBean>()
        val areaList: ArrayList<AreaBean> = ArrayList<AreaBean>()
        val streetList: ArrayList<AreaBean> = ArrayList<AreaBean>()
        val city_index = IntArray(1)

        val province = arrayOf("")
        val city = arrayOf("")
        val area = arrayOf("")
        val builder = StringBuilder()
        val pos0 = IntArray(1)
        val pos1 = IntArray(1)
        var selectedProvince: String? = null
        var selectedCity: String? = null
        var selectedArea: String? = null
        var selecedStreet: String? = null
        var pos2: Int
        var pos3: Int
        val json2: JSONObject = JSONObject(JsonReadHelper.readJson(this, "pcas.json"))
        val jsonArrayStr = json2.optJSONArray("provinceList")?.toString()
        val area_models: List<Y_Public_Area_Model> = Gson().fromJson<List<Y_Public_Area_Model>>(
            jsonArrayStr,
            object : TypeToken<List<Y_Public_Area_Model?>?>() {}.type
        )
        for (bean in area_models) {
            provinceList.add(AreaBean(bean.code, bean.name))
        }
        AreaSelector(this)
            .setOnItemClickListener(object : OnSelectorItemClickListener {
                override fun setProvinceList(): List<AreaBean?>? {
                    return provinceList
                }

                override fun setOnProvinceSelected(
                    areaBean: AreaBean,
                    position: Int
                ): List<AreaBean?>? {
                    pos0[0] = position
                    province[0] = areaBean.getName()
                    builder.append(province[0])
                    selectedProvince = provinceList[position].name
                    //btn.setText(builder)
                    if (!cityList.isEmpty()) {
                        cityList.clear()
                    }
                    for (bean in area_models[position].children) {
                        cityList.add(AreaBean(bean.code, bean.name))
                    }
                    return cityList
                }

                override fun setOnCitySelected(
                    areaBean: AreaBean,
                    position: Int
                ): List<AreaBean?>? {
                    pos1[0] = position
                    city[0] = areaBean.getName()
                    city_index[0] = position
                    builder.delete(0, builder.length)
                    builder.append(province[0]).append(city[0])
                    selectedCity = cityList[position].name
                    if (!areaList.isEmpty()) {
                        areaList.clear()
                    }
                    for (bean in area_models[pos0[0]].children[position].children) {
                        areaList.add(AreaBean(bean.code, bean.name))
                    }
                    return areaList
                }

                override fun setOnAreaSelected(
                    areaBean: AreaBean,
                    position: Int
                ): List<AreaBean?>? {
                    area[0] = areaBean.getName()
                    builder.delete(0, builder.length)
                    builder.append(province[0]).append(city[0]).append(area[0])
                    selectedArea = areaList[position].name
                    if (!streetList.isEmpty()) {
                        streetList.clear()
                    }
                    for (bean in area_models[pos0[0]].children[pos1[0]].children[position].children) {
                        streetList.add(AreaBean(bean.code, bean.name))
                    }
                    return streetList
                }

                override fun setOnStreetSelected(areaBean: AreaBean, position: Int) {
                    builder.append(areaBean.getName())
                    et_phone.text = builder
                    selecedStreet = streetList[position].name
                    showLoadingView()
                    mViewModel.updateUserInfo(
                        Constant.TOKEN,
                        mUserInfo?.id!!,
                        null,
                        selectedProvince,
                        selectedCity,
                        selectedArea,
                        selecedStreet,
                        null,
                        null
                    )


                }
            })
            .build()
            .showSelector()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        postUpdateMessage()
    }

}