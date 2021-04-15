package com.zues.ruiyu.bangwoqu.ocr

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.zues.ruiyu.bangwoqu.R
import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssDeviceHelper

/**
 *@Author liforent
 *@create 2020/11/26 11:07
 */
class OcrViewFinder(context: Context, attributeSet: AttributeSet) :
    RelativeLayout(context, attributeSet) {
    private lateinit var mPointPaint: Paint
    private lateinit var mTextPaint: Paint
    private lateinit var mPaint: Paint
    private val OPAQUE = 0xFF
    private var tipsText: String = ""
    private val SCANNER_ALPHA = intArrayOf(0, 64, 128, 192, 255, 192, 128, 64)

    private var mScannerAlpha = 0
    private val mMaskColor = resources.getColor(R.color.finder_mask, null)

    private val mFrameColor = resources.getColor(R.color.white, null)
    private val mLaserColor = resources.getColor(R.color.finder_laser, null) //四个脚的颜色
    private val mTextColor = 0xffffffff
    private val mFocusThick = 0
    private val mAngleThick = 0
    private val mAngleLength = 0
    private val mTextSize = 26f
    private val scrRes: Point? = ZssDeviceHelper.getScreenResolution(context)
    private lateinit var mFrameRect: Rect

    init {
        mPointPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint.color = mTextColor.toInt()
        mTextPaint.textSize = mTextSize
        // 需要调用下面的方法才会执行onDraw方法
        setWillNotDraw(false)
        val width = ZssDeviceHelper.getScreenWidth(context)
        val height = ZssDeviceHelper.getScreenHeight(context)
        mFrameRect = Rect(0, 0, width, height)
        //mRect = mFrameRect

    }

    fun getRect(): Rect? {
        return mFrameRect
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val frame = mFrameRect ?: return
        val width = width
        val height = height

        // 绘制焦点框外边的暗色背景
        mPaint.color = mMaskColor
        canvas!!.drawRect(0f, 0f, width.toFloat(), frame.top.toFloat(), mPaint)
        canvas.drawRect(
            0f,
            frame.top.toFloat(),
            frame.left.toFloat(),
            frame.bottom + 1.toFloat(),
            mPaint
        )
        canvas.drawRect(
            frame.right + 1.toFloat(),
            frame.top.toFloat(),
            width.toFloat(),
            frame.bottom + 1.toFloat(),
            mPaint
        )
        canvas.drawRect(0f, frame.bottom + 1.toFloat(), width.toFloat(), height.toFloat(), mPaint)
        drawFocusRect(canvas, frame)
        drawAngle(canvas, frame)
        drawText(canvas, frame)
        drawLaser(canvas, frame)
    }


    /**
     * 画聚焦框，白色的
     *
     * @param canvas
     * @param rect
     */
    private fun drawFocusRect(canvas: Canvas, rect: Rect) {
        // 绘制焦点框（黑色）
        mPaint.color = mFrameColor
        // 上
        canvas.drawRect(
            rect.left + mAngleLength.toFloat(),
            rect.top.toFloat(),
            rect.right - mAngleLength.toFloat(),
            rect.top + mFocusThick.toFloat(),
            mPaint
        )
        // 左
        canvas.drawRect(
            rect.left.toFloat(),
            rect.top + mAngleLength.toFloat(),
            rect.left + mFocusThick.toFloat(),
            rect.bottom - mAngleLength.toFloat(),
            mPaint
        )
        // 右
        canvas.drawRect(
            rect.right - mFocusThick.toFloat(),
            rect.top + mAngleLength.toFloat(),
            rect.right.toFloat(),
            rect.bottom - mAngleLength.toFloat(),
            mPaint
        )
        // 下
        canvas.drawRect(
            rect.left + mAngleLength.toFloat(),
            rect.bottom - mFocusThick.toFloat(),
            rect.right - mAngleLength.toFloat(),
            rect.bottom.toFloat(),
            mPaint
        )
    }

    /**
     * 画四个角
     *
     * @param canvas
     * @param rect
     */
    private fun drawAngle(canvas: Canvas, rect: Rect) {
        mPaint.color = mLaserColor
        mPaint.alpha = OPAQUE
        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = mAngleThick.toFloat()
        val left = rect.left
        val top = rect.top
        val right = rect.right
        val bottom = rect.bottom
        // 左上角
        canvas.drawRect(
            left.toFloat(),
            top.toFloat(),
            left + mAngleLength.toFloat(),
            top + mAngleThick.toFloat(),
            mPaint
        )
        canvas.drawRect(
            left.toFloat(),
            top.toFloat(),
            left + mAngleThick.toFloat(),
            top + mAngleLength.toFloat(),
            mPaint
        )
        // 右上角
        canvas.drawRect(
            right - mAngleLength.toFloat(),
            top.toFloat(),
            right.toFloat(),
            top + mAngleThick.toFloat(),
            mPaint
        )
        canvas.drawRect(
            right - mAngleThick.toFloat(),
            top.toFloat(),
            right.toFloat(),
            top + mAngleLength.toFloat(),
            mPaint
        )
        // 左下角
        canvas.drawRect(
            left.toFloat(),
            bottom - mAngleLength.toFloat(),
            left + mAngleThick.toFloat(),
            bottom.toFloat(),
            mPaint
        )
        canvas.drawRect(
            left.toFloat(),
            bottom - mAngleThick.toFloat(),
            left + mAngleLength.toFloat(),
            bottom.toFloat(),
            mPaint
        )
        // 右下角
        canvas.drawRect(
            right - mAngleLength.toFloat(),
            bottom - mAngleThick.toFloat(),
            right.toFloat(),
            bottom.toFloat(),
            mPaint
        )
        canvas.drawRect(
            right - mAngleThick.toFloat(),
            bottom - mAngleLength.toFloat(),
            right.toFloat(),
            bottom.toFloat(),
            mPaint
        )
    }

    private fun drawText(canvas: Canvas, rect: Rect) {
        val margin = 40
        mPaint.color = resources.getColor(R.color.white, null)
        mPaint.textSize = resources.getDimension(R.dimen.text_size_13sp)
        val fontMetrics = mPaint.fontMetrics
        val fontTotalHeight = fontMetrics.bottom - fontMetrics.top
        val offY = fontTotalHeight / 2 - fontMetrics.bottom
        val newY = rect.top - margin - offY
        val left: Float =
            (ZssDeviceHelper.getScreenWidth(context) - mPaint.textSize * tipsText.length) / 2
        canvas.drawText(tipsText, left, 100f, mPaint)
    }

    private fun drawLaser(canvas: Canvas, rect: Rect) {
        // 绘制焦点框内固定的一条扫描线
        mPaint.color = mLaserColor
        mPaint.alpha = SCANNER_ALPHA[mScannerAlpha]
        mScannerAlpha = (mScannerAlpha + 1) % SCANNER_ALPHA.size
        val middle = rect.height() / 2 + rect.top
        canvas.drawRect(
            rect.left + 2.toFloat(),
            middle - 1.toFloat(),
            rect.right - 1.toFloat(),
            middle + 2.toFloat(),
            mPaint
        )
        postInvalidateDelayed(100)
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
       // mHandler.removeMessages(1)
    }

    fun setTipsText(tips: String) {
        tipsText = tips
        this.postInvalidate()
    }
}