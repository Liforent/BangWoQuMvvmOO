package com.zues.ruiyu.bangwoqu.module.account.view

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.areaselectorlibrary.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.BaseApplication
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleActivity
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.base.https.onResponse
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssSPHelper
import com.zues.ruiyu.bangwoqu.module.account.data.RegisterModel
import com.zues.ruiyu.bangwoqu.module.account.AccountViewModel
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *@Author liforent
 *@create 2020/9/8 18:12
 */
class RegisterActivity : BaseLifeCycleActivity<AccountViewModel>() {
    var mProvince = ""
    var mCounty = ""
    var mCity = ""
    var mStreet = ""
    override fun initData(): Boolean = false
    override fun initDataObserver() {
        mViewModel.mVerificationCodeData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                //发送成功code为1, 否则发送失败。不返回data
                tv_get_code.startCountDown(59)
            }
        })

        mViewModel.mAccountData.observe(this, {
            it.onResponse(mViewModel.loadState) {
                (application as BaseApplication).updateAccountInfo(it.data)
                ZssSPHelper.getInstance(this@RegisterActivity)
                    .put(Constant.SP_KEY_TOKEN, "JWT " + it.data.token)
                ZssSPHelper.getInstance(this@RegisterActivity).put(
                    Constant.SP_KEY_USER_ID,
                    it.data.id
                )
                startActivity(Intent(this@RegisterActivity, RegisterSuccessActivity::class.java))
            }

        })

    }

    override fun getLayoutId(): Int = R.layout.activity_register

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        super.initView()
        title_view.setBack {
            finish()
        }
        tv_get_code
            .setNormalText("获取验证码")
            .setCountDownText("", "后重发")
            .setCloseKeepCountDown(true) //关闭页面保持倒计时开关
            .setCountDownClickable(false) //倒计时期间点击事件是否生效开关
            .setShowFormatTime(true) //是否格式化时间
            .setIntervalUnit(TimeUnit.SECONDS)
            .setOnCountDownStartListener {}
            .setOnCountDownTickListener {}
            .setOnCountDownFinishListener { tv_get_code!!.text = "获取验证码" }

        tv_login.setOnClickListener {

            if (et_name.text.toString() == "") {
                toast("快递点名称不能为空！")
                return@setOnClickListener
            }
            if (et_name.text.toString() == "") {
                toast("手机号码不能为空！")
                return@setOnClickListener
            }
            if (et_verification_code.text.toString() == "") {
                toast("验证码不能为空！")
                return@setOnClickListener
            }
            if (et_phone.text.toString() == "") {
                toast("所在地区不能为空！")
                return@setOnClickListener
            }
            if (et_detail_address.text.toString() == "") {
                toast("详细地址不能为空！")
                return@setOnClickListener
            }
            if (et_id_number.text.toString() == "") {
                toast("负责人姓名不能为空！")
                return@setOnClickListener
            }
            if (et_id_number.text.toString() == "") {
                toast("身份证号码不能为空！")
                return@setOnClickListener
            }
            showLoadingView()
            mViewModel.register(
                RegisterModel(
                    Constant.AUTH_TYPE_REGISTER,
                    et_verification_code.text.toString(),
                    et_shipper_name.text.toString().trim(),
                    et_phone.text.toString().trim(),
                    et_name.text.toString().trim(),
                    et_id_number.text.toString().trim(),
                    mProvince,
                    mCity,
                    mCounty,
                    mStreet,
                    et_detail_address.text.toString().trim(),
                    Constant.ROLE_TYPE_DEFAULT
                )
            )
        }
        tv_get_code.setOnClickListener {
            if (et_phone.text.isNullOrEmpty() || et_phone.text.length != 11) {
                Toast.makeText(getApplication(), R.string.wrong_phone_number, Toast.LENGTH_SHORT)
                    .show()
            } else {
                showLoadingView()
                mViewModel.getVerificationCode(
                    et_phone.text.toString(),
                    Constant.AUTH_TYPE_REGISTER
                )
            }

        }
        et_place.setOnClickListener {
            onAddressPicker(et_place)
        }
    }


    fun onAddressPicker(view: View?) {
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
                    //btn.setText(builder)
                    if (!cityList.isEmpty()) {
                        cityList.clear()
                    }
                    for (bean in area_models[position].children) {
                        cityList.add(AreaBean(bean.code, bean.name))
                    }
                    mProvince = areaBean.name
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
                    if (!areaList.isEmpty()) {
                        areaList.clear()
                    }
                    for (bean in area_models[pos0[0]].children[position].children) {
                        areaList.add(AreaBean(bean.code, bean.name))
                    }
                    mCity = areaBean.name

                    return areaList
                }

                override fun setOnAreaSelected(
                    areaBean: AreaBean,
                    position: Int
                ): List<AreaBean?>? {
                    area[0] = areaBean.getName()
                    builder.delete(0, builder.length)
                    builder.append(province[0]).append(city[0]).append(area[0])
                    if (!streetList.isEmpty()) {
                        streetList.clear()
                    }
                    for (bean in area_models[pos0[0]].children[pos1[0]].children[position].children) {
                        streetList.add(AreaBean(bean.code, bean.name))
                    }
                    mCounty = areaBean.name

                    return streetList
                }

                override fun setOnStreetSelected(areaBean: AreaBean, position: Int) {
                    builder.append(areaBean.getName())
                    (view as TextView).text = builder
                    mStreet = areaBean.name
                    iv_right_arrow.visibility = View.INVISIBLE
                }
            })
            .build()
            .showSelector()
    }

}