package com.zues.ruiyu.bangwoqu.custom

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.R.*
import com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.show
import kotlinx.android.synthetic.main.zss_title_view.view.*

class ZssTitleView : RelativeLayout {
    private lateinit var view: View

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
//        val lp = this.layoutParams
//        val topSafeHeight = ZssStatusBarUtil.getStatusBarHeight(this.context)
//        lp.height += topSafeHeight
//        this.layoutParams = lp
//        setPadding(
//                paddingStart,
//                paddingTop + topSafeHeight,
//                paddingEnd,
//                paddingBottom
//        )
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        view = LayoutInflater.from(context).inflate(layout.zss_title_view, this, true)
        val typedArray = context.obtainStyledAttributes(attrs, styleable.ZssTitleView)
        val strTvLeft: String?
        val leftImgResId: Int
        val strTitleText: String?
        val rightImgResId: Int
        val titleTextColor: Int
        val titleTextSize: Float
        try {
            typedArray.apply {
                leftImgResId =
                    getResourceId(styleable.ZssTitleView_left_img_res, drawable.ic_back_black)
//                rightImgResId = getResourceId(styleable.ZssTitleView_right_img_res, drawable.zy_ic_refresh)
                titleTextColor = getColor(
                    styleable.ZssTitleView_ztv_title_text_color,
                    ContextCompat.getColor(context, color.black)
                )
                titleTextSize = getFloat(styleable.ZssTitleView_ztv_title_text_size, 18f)
                strTvLeft = getString(styleable.ZssTitleView_ztv_tv_left)
                strTitleText = getString(styleable.ZssTitleView_title_text)
            }
        } finally {
            typedArray.recycle()
        }
        view.apply {
            tv_left.apply {
                text = strTvLeft
                visibility = View.GONE
            }
            tv_title.apply {
                setTextColor(titleTextColor)
                text = strTitleText
                textSize = titleTextSize
            }
        }
        Glide.with(context).load(leftImgResId).into(view.iv_left)
//        Glide.with(context).load(rightImgResId).into(view.iv_right)
        setRightImgVisible(false)
    }

    fun setTitleText(title: String?) {
        tv_title.text = title
    }

    fun setTitleTextVisible(visible: Boolean) {
        tv_title.visibility = if (visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun addViewToTitleContainer(view: View) {
        fl_title_container.addView(view)
    }

    fun removeViewFromTitleContainer(view: View) {
        fl_title_container.removeView(view)
    }

    fun setBack(onClickListener: () -> Unit) {
        iv_left.setOnClickListener { onClickListener() }
        tv_left.setOnClickListener { onClickListener() }
    }

    fun setBack(onClickListener: OnClickListener) {
        iv_left.setOnClickListener(onClickListener)
    }

    fun setRightImgVisible(visible: Boolean) {
        iv_right.visibility = if (visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun setLeftImgVisible(visible: Boolean) {
        iv_left.visibility = if (visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun setRightImgOnclick(onClickListener: () -> Unit) {
        iv_right.setOnClickListener { onClickListener() }
    }

    fun setTvLeftVisible(visible: Boolean) {
        tv_left.visibility = if (visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    public fun setRightTextVisibiliy(visible: Boolean) {
        if (visible) {
            tv_right.show()
        }
    }

    public fun setRightTextOnclickListener(onClickListener: () -> Unit) {
        tv_right.setOnClickListener { onClickListener() }
    }

    public override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.fontScale != 1f) //非默认值
            resources
        super.onConfigurationChanged(newConfig)
    }

    override fun getResources(): Resources {
        val res = super.getResources()
        if (res.configuration.fontScale != 1f) { //非默认值
            val newConfig = Configuration()
            newConfig.setToDefaults() //设置默认
            context.createConfigurationContext(newConfig)
        }
        return res
    }
}