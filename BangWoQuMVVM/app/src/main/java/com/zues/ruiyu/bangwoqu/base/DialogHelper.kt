package com.zues.ruiyu.bangwoqu.base

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.zues.ruiyu.bangwoqu.module.account.view.EditInfoDialogFragment
import com.zues.ruiyu.bangwoqu.module.home.view.ModifyPhoneDialog
import com.zues.ruiyu.bangwoqu.module.home.view.VerifyPhoneDialog

/**
 *@Author liforent
 *@create 2020/9/11 11:14
 */
object DialogHelper {
    fun showComfirmPhoneDialog(
        fragmentManager: FragmentManager,
        listener: VerifyPhoneDialog.OnConfirmListener
    ) {
        VerifyPhoneDialog().apply {
            this.listener = listener
            show(fragmentManager, this::class.qualifiedName)
        }
    }

    fun showEditDialog(
        supportFragmentManager: FragmentManager,
        title: String,
        hint: String,
        clickListener: EditInfoDialogFragment.DFOnClickListener
    ) {
        val editInfoDialogFragment = EditInfoDialogFragment(clickListener)
        val bundle = Bundle()
        bundle.putString(Constant.DATA_EDIT_TITLE, title)
        bundle.putString(Constant.DATA_EDIT_HINT, hint)
        editInfoDialogFragment.arguments = bundle
        editInfoDialogFragment.show(
            supportFragmentManager,
            EditInfoDialogFragment::class.qualifiedName
        )
        editInfoDialogFragment.setOnclick(clickListener)
    }

    fun showModifyPhoneDialog(
        supportFragmentManager: FragmentManager,
        trackingNumber: String,
        id: Int,
        fee: Float,
        clickListener: ModifyPhoneDialog.OnBtnClickListener
    ) {

        ModifyPhoneDialog(clickListener).apply {
            val bundle = Bundle()
            bundle.putString(Constant.DATA_TRACKING_NUMBER, trackingNumber)
            bundle.putInt(Constant.DATA_PACKAGE_ID, id)
            bundle.putFloat(Constant.DATA_FEE, fee)
            arguments = bundle
            show(supportFragmentManager, this::class.qualifiedName)
        }
    }


}