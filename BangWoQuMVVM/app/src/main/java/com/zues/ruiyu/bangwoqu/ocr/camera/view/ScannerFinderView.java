package com.zues.ruiyu.bangwoqu.ocr.camera.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.zues.ruiyu.bangwoqu.R;

import com.zues.ruiyu.bangwoqu.base.commonUtils.ZssDeviceHelper;

import static com.zues.ruiyu.bangwoqu.base.commonUtils.ext.MyExtensionFuck.getPhone;


public final class ScannerFinderView extends RelativeLayout {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 100L;
    private static final int OPAQUE = 0xFF;

    private static final int MIN_FOCUS_BOX_WIDTH = 50;
    private static final int MIN_FOCUS_BOX_HEIGHT = 50;
    private static final int MIN_FOCUS_BOX_TOP = 200;

    public static final int SCAN_MODE_ALL = 100;
    public static final int SCAN_MODE_PHONE = 101;
    public static final int SCAN_MODE_V1_2 = 102;

    private static Point scrRes;

    private final Paint mPaint;
    private int mScannerAlpha;
    private final int mMaskColor;
    private final int mFrameColor;
    private final int mLaserColor;
    private final int mTextColor;
    private final int mFocusThick;
    private final int mAngleThick;
    private final int mAngleLength;
    private int mFocusWidth;
    private int mFocusHeight;
    private int left;
    private int top;

    private String tipsText;
    private int scanMode = SCAN_MODE_ALL;


    private Rect mFrameRect; //绘制的Rect
    private Rect mRect; //返回的Rect

    public ScannerFinderView(Context context) {
        this(context, null);
    }

    public ScannerFinderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScannerFinderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        Resources resources = getResources();
        mMaskColor = resources.getColor(R.color.finder_mask);
        mFrameColor = resources.getColor(R.color.white);
        mLaserColor = resources.getColor(R.color.finder_laser);
        mTextColor = Color.parseColor("#767B81");
        scrRes = ZssDeviceHelper.getScreenResolution(context);
        mFocusThick = 2;
        mAngleThick = 8;
        mAngleLength = 40;
        mScannerAlpha = 0;
        init(context);
        //this.setOnTouchListener(getTouchListener());
    }

    private void init(Context context) {
        this.setTipsText("请扫描快递单号");
        if (isInEditMode()) {
            return;
        }
        // 需要调用下面的方法才会执行onDraw方法
        setWillNotDraw(false);
        if (mFrameRect == null) {
            mFocusWidth = ZssDeviceHelper.getScreenWidth(context) * 4 / 5;
            mFocusHeight = (int) (mFocusWidth * 1.0 / 315 * 100);
            //mFrameRect = new Rect(left, top, left + width, top + height);
        }
    }

    public Rect getRect() {
        return mRect;
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            return;
        }
        mFocusWidth = ZssDeviceHelper.getScreenWidth(getContext()) - ZssDeviceHelper.dp2px(getContext(), 40f);
        if (scanMode == SCAN_MODE_ALL) {
            top = ZssDeviceHelper.dp2px(getContext(), 100);
            mFocusHeight = ZssDeviceHelper.getScreenHeight(getContext()) / 2;
        } else if (scanMode == SCAN_MODE_PHONE) {
            top = ZssDeviceHelper.dp2px(getContext(), 200);
            mFocusHeight = 300;
        } else {
            mFocusWidth = ZssDeviceHelper.getScreenWidth(getContext()) * 4 / 5;
            mFocusHeight = (int) (mFocusWidth * 1.0 / 315 * 100);
            top = ZssDeviceHelper.dp2px(getContext(), 150);
        }
        left = (ZssDeviceHelper.getScreenWidth(getContext()) - mFocusWidth) / 2;
        mFrameRect = new Rect(left, top, left + mFocusWidth, top + mFocusHeight);

        Rect frame = mFrameRect;
        mRect = mFrameRect;

        int width = getWidth();
        int height = getHeight();

        // 绘制焦点框外边的暗色背景
        mPaint.setColor(mMaskColor);
        canvas.drawRect(0, 0, width, frame.top, mPaint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, mPaint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, mPaint);
        canvas.drawRect(0, frame.bottom + 1, width, height, mPaint);

        drawFocusRect(canvas, frame);
        drawAngle(canvas, frame);
        drawText(canvas, frame);
        //drawLaser(canvas, frame);
    }

    /**
     * 画聚焦框，白色的
     *
     *
     *
     */
    private void drawFocusRect(Canvas canvas, Rect rect) {
        // 绘制焦点框（黑色）
        mPaint.setColor(mFrameColor);
        // 上
        canvas.drawRect(rect.left + mAngleLength, rect.top, rect.right - mAngleLength, rect.top + mFocusThick, mPaint);
        // 左
        canvas.drawRect(rect.left, rect.top + mAngleLength, rect.left + mFocusThick, rect.bottom - mAngleLength,
                mPaint);
        // 右
        canvas.drawRect(rect.right - mFocusThick, rect.top + mAngleLength, rect.right, rect.bottom - mAngleLength,
                mPaint);
        // 下
        canvas.drawRect(rect.left + mAngleLength, rect.bottom - mFocusThick, rect.right - mAngleLength, rect.bottom,
                mPaint);
    }

    /**
     * 画四个角
     *
     *
     *
     */
    private void drawAngle(Canvas canvas, Rect rect) {
        mPaint.setColor(mLaserColor);
        mPaint.setAlpha(OPAQUE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(mAngleThick);
        int left = rect.left;
        int top = rect.top;
        int right = rect.right;
        int bottom = rect.bottom;
        // 左上角
        canvas.drawRect(left, top, left + mAngleLength, top + mAngleThick, mPaint);
        canvas.drawRect(left, top, left + mAngleThick, top + mAngleLength, mPaint);
        // 右上角
        canvas.drawRect(right - mAngleLength, top, right, top + mAngleThick, mPaint);
        canvas.drawRect(right - mAngleThick, top, right, top + mAngleLength, mPaint);
        // 左下角
        canvas.drawRect(left, bottom - mAngleLength, left + mAngleThick, bottom, mPaint);
        canvas.drawRect(left, bottom - mAngleThick, left + mAngleLength, bottom, mPaint);
        // 右下角
        canvas.drawRect(right - mAngleLength, bottom - mAngleThick, right, bottom, mPaint);
        canvas.drawRect(right - mAngleThick, bottom - mAngleLength, right, bottom, mPaint);
    }

    private void drawText(Canvas canvas, Rect rect) {
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(ZssDeviceHelper.dp2px(getContext(), 34f));
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float fontTotalHeight = fontMetrics.bottom - fontMetrics.top;
        float offY = fontTotalHeight / 2 - fontMetrics.bottom;
        float newY;
        if (scanMode == SCAN_MODE_ALL) {
            Rect rect1 = new Rect();
            mPaint.getTextBounds(tipsText, 0, tipsText.length(), rect1);
            float tipHeight = rect1.height();
            newY = (float) ((rect.top + rect.bottom + tipHeight) * 1.0 / 2);
        } else {
            newY = getRect().bottom + ZssDeviceHelper.dp2px(getContext(), 30);
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(ZssDeviceHelper.dp2px(getContext(), 15f));
        }

        float left = (ZssDeviceHelper.getScreenWidth(getContext()) - mPaint.getTextSize() * tipsText.length()) / 2;
        canvas.drawText(tipsText, left, newY, mPaint);
    }

    private void drawLaser(Canvas canvas, Rect rect) {
        // 绘制焦点框内固定的一条扫描线
        mPaint.setColor(mLaserColor);
        mPaint.setAlpha(SCANNER_ALPHA[mScannerAlpha]);
        mScannerAlpha = (mScannerAlpha + 1) % SCANNER_ALPHA.length;
        int middle = rect.height() / 2 + rect.top;
        canvas.drawRect(rect.left + 2, middle - 1, rect.right - 1, middle + 2, mPaint);

        mHandler.sendEmptyMessageDelayed(1, ANIMATION_DELAY);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            invalidate();
        }
    };


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(1);
    }

    public void setTipsText(String tips) {
        tipsText = tips;
        this.postInvalidate();
    }

    public void setScanMode(int mode) {
        scanMode = mode;
        postInvalidate();
    }

    public int getScanMode() {
        return scanMode;
    }
}
