package com.zues.ruiyu.bangwoqu.base.commonUtils.ext

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.zues.ruiyu.bangwoqu.base.ZLog
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssDeviceHelper
import com.zues.ruiyu.bangwoqu.module.home.view.ScanInActivity
import kotlinx.android.synthetic.main.layout_edit_package_info.*
import org.jetbrains.anko.displayMetrics
import java.lang.StringBuilder
import java.util.regex.Pattern

/**
 *          扩展函数
 *@Author liforent
 *@create 2020/9/24 9:58
 */

object MyExtensionFuck {

    //计算宽高
    fun View.getExtHeight(
        designWidth: Int,
        designHeight: Int,
        context: Context,
        finalWidth: Int?
    ): Int {
        val mFinalWidth = finalWidth ?: ZssDeviceHelper.getScreenWidth(context)
        return (designHeight * 1.0 / designWidth * mFinalWidth).toInt()
    }


    //常用扩展
    fun View.gone() {
        this.visibility = View.GONE
    }

    fun View.invisible() {
        this.visibility = View.INVISIBLE
    }

    fun View.show() {
        this.visibility = View.VISIBLE
    }

    fun Int.dp2px(context: Context): Int {
        return (context.displayMetrics.density * this + 0.5f).toInt()
    }

    fun Int.px2dp(context: Context): Int {
        return (this / (context.displayMetrics.density + 0.5f)).toInt()
    }

    //去除字符串中的非字符，如果得到的str里存在手机号码（11位，1开头。）则返回手机号码，否则返回""。
    @JvmStatic
    fun getPhoneNumberFromString(str: String): String {
        val mStr = replace(str)
        val index = mStr.indexOfFirst { it == '1' }
        ZLog.e("str:$mStr  index:$index   length:${mStr.length}  ")
        return if (index >= 0 && mStr.length >= index + 11) {
            mStr.substring(index, index + 11)
        } else {
            ""
        }
    }

    //去除字符串中的非字符
    fun replace(text: String): String {
        val mStr = text.replace("\\D+".toRegex(), "") //把非数字替换为空值;
        return if (mStr.length > 10) mStr else ""
    }


    //获取字符串中的手机号码
    @JvmStatic
    fun getPhone(str: String): String {
        val regex = "1[345789]\\d{9}"
        val p = Pattern.compile(regex)    //获取正则表达式
        val m = p.matcher(str)        //获取匹配器
        val strB = StringBuilder("")
        while (m.find()) {//看是否能在字符串中找到符合正则表达式的字符串，找到返回true，同时指针指向下一个子序列
            val phone = m.group()            //必须先找再获取
            strB.append("$phone ")
        }
        return strB.toString()
    }


    //保留一位小数
    fun EditText.addMyDecimalWatcher(afterChanged: (s: Editable?) -> Unit) {
        val mDecimalDigits = 1
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //只保留一位小数
                if (s.toString().contains(".")) {
                    if (s!!.length - 1 - s.toString()
                            .indexOf(".") > mDecimalDigits
                    ) {
                        val ms = s.toString().subSequence(
                            0,
                            s.toString().indexOf(".") + mDecimalDigits + 1
                        )
                        this@addMyDecimalWatcher.setText(ms)
                        this@addMyDecimalWatcher.setSelection(ms.length)
                    }
                }
                if (s.toString().trim().substring(0) == ".") {
                    val ms = "0$s"
                    this@addMyDecimalWatcher.setText(ms)
                    this@addMyDecimalWatcher.setSelection(2)
                }
                if (s.toString().startsWith("0")
                    && s.toString().trim().length > 1
                ) {
                    if (s.toString().substring(1, 2) != ".") {
                        this@addMyDecimalWatcher.setText(s!!.subSequence(0, 1))
                        this@addMyDecimalWatcher.setSelection(1)
                        return
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                this@addMyDecimalWatcher.removeTextChangedListener(this)
                afterChanged(s)
                this@addMyDecimalWatcher.addTextChangedListener(this)

            }
        })
    }

    fun EditText.addWatcher(afterChanged: (s: Editable?) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                afterChanged(s)
            }
        })
    }



}