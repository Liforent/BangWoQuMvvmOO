package com.zues.ruiyu.bangwoqu.module.home.view

import android.graphics.Color
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.mvvm.BaseLifeCycleDialogFragment
import com.zues.ruiyu.bangwoqu.base.Constant
import com.zues.ruiyu.bangwoqu.base.commonUtils.InputMethodUtils
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.show
import com.zues.ruiyu.bangwoqu.module.home.data.ScannerTempModel
import com.zues.ruiyu.bangwoqu.module.home.HomeViewModel
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor


/**
 *@Author liforent
 *@create 2020/9/11 10:57
 */
class ModifyPhoneDialog(val listener: OnBtnClickListener) :
    BaseLifeCycleDialogFragment<HomeViewModel>() {
    var trackingNumber: String? = null
    var packge_id: Int? = 0
    var fee: Float? = null
    lateinit var etPhone: EditText
    override fun getContentLayoutId(): Int = R.layout.df_modify_phone


    override fun handleViewCreate(view: View) {
        super.handleViewCreate(view)
        trackingNumber = arguments?.getString(Constant.DATA_TRACKING_NUMBER, "")
        packge_id = arguments?.getInt(Constant.DATA_PACKAGE_ID, 0)
        fee = arguments?.getFloat(Constant.DATA_FEE, 1f)
        view.findViewById<TextView>(R.id.tv_tracking_number).text = "快递单号 ${trackingNumber}"
        etPhone = view.findViewById(R.id.et_phone)
        val clRoot = view.findViewById<ConstraintLayout>(R.id.cl_root)
        InputMethodUtils.openKeyBoard(requireActivity(), etPhone)
        val tvSave = view.findViewById<TextView>(R.id.tv_save)
        tvSave.apply {
            text = "取消"
            textColor = Color.parseColor("#FF9A9A9A")
            background = resources.getDrawable(R.drawable.bg_r8fe, null)
            setOnClickListener {
                if ("保存".equals(tvSave.text)) {
                    val phone = etPhone.text.toString()
                    if (phone.length != 11) {
                        Toast.makeText(requireContext(), "请输入正确的手机号码", Toast.LENGTH_SHORT).show()
                    } else {
                        mViewModel.modifyPhone(packge_id!!, phone)
                    }
                } else {
                    listener.onModifyPhoneResult(false, null)
                    InputMethodUtils.closeKeyBoard(requireActivity(), etPhone)
                    dismiss()
                }
            }
        }
        val layoutListener = object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val r = Rect()
                clRoot.getWindowVisibleDisplayFrame(r)
                val computeUsableHeight = r.bottom - r.top

                //比较布局变化前后的View的可用高度
                var usableHeightNow = computeUsableHeight - 73
                var usableHeightPrevious = 0
                if (usableHeightNow != usableHeightPrevious) {
                    //如果两次高度不一致
                    //将当前的View的可用高度设置成View的实际高度
//                    val frameLayoutParams = clRoot.layoutParams
//                    frameLayoutParams.height = usableHeightNow
                    clRoot.setPadding(0, 0, 0, 73)
                    clRoot.requestLayout() //请求重新布局
                    clRoot.show()
                    usableHeightPrevious = usableHeightNow
                }

            }
        }

        // clRoot.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)

        etPhone.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {

                    if (s?.length == 11) {
                        tvSave.text = "保存"
                        tvSave.textColor = Color.parseColor("#FFFFFFFF")
                        tvSave.background =
                            ResourcesCompat.getDrawable(resources, R.drawable.bg_r8_blue, null)
                    } else {
                        tvSave.text = "取消"
                        tvSave.textColor = Color.parseColor("#FF9A9A9A")
                        tvSave.background =
                            ResourcesCompat.getDrawable(resources, R.drawable.bg_r8fe, null)
                    }
                }
            })
        }

    }


    public interface OnBtnClickListener {
        fun onModifyPhoneResult(result: Boolean, data: ScannerTempModel?)
    }

    override fun initDataObserver() {
        mViewModel.mModifyPhoneData.observe(this, {
            if (it.code == Constant.SUCCESS) {
                listener.onModifyPhoneResult(
                    true,
                    ScannerTempModel(
                        etPhone.text.toString(),
                        trackingNumber!!,
                        fee?.toInt().toString(),
                        packge_id!!
                    )
                )
                Toast.makeText(requireContext(), "修改成功！", Toast.LENGTH_SHORT).show()
                InputMethodUtils.closeKeyBoard(requireActivity(), etPhone)
                dismiss()
            } else {
                toast("修改失败${it?.msg}")
            }
            showSuccess()
        })
    }

    override fun dismiss() {
        InputMethodUtils.closeKeyBoard(requireActivity(), etPhone)
        super.dismiss()
    }


}