package com.zues.ruiyu.bangwoqu.base.commonUtils

import android.text.InputFilter
import android.text.Spanned

/**
 *@Author liforent
 *@create 2020/9/16 14:57
 */
class InputFilterMinMax(var min: Int = 0, var max: Int = 0) : InputFilter {


    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence ?{
        try {
            val input = (dest.toString() + source.toString()).toInt()
            if (isInRange(min, max, input)) return null
        } catch (nfe: Exception) {
        }
        return ""
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) c >= a && c <= b else c >= b && c <= a
    }
}