package com.zues.ruiyu.bangwoqu.custom;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.zues.ruiyu.bangwoqu.R;
import com.zues.ruiyu.bangwoqu.base.commonUtils.SoftKeyboardStateHelper;
import com.zues.ruiyu.bangwoqu.base.ZLog;

/**
 * @Author liforent
 * @create 2020/10/14 9:57
 */
public class TestLayout extends RelativeLayout implements SoftKeyboardStateHelper.SoftKeyboardStateListener {
    private Context context;
    private SoftKeyboardStateHelper mKeyboardHelper;
    private EditText mEtMsg;
    private RelativeLayout mRlFace;


    public TestLayout(Context context) {
        super(context);
        init(context);
    }

    public TestLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TestLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        View root = View.inflate(context, R.layout.soft_pan_fixed_et, null);
        this.addView(root);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initData();
        initWidget();
    }

    private void initWidget() {
        mEtMsg = (EditText) findViewById(R.id.toolbox_et_message);
        // 点击消息输入框
        mEtMsg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLayout();
            }
        });
        mRlFace = (RelativeLayout) findViewById(R.id.toolbox_layout_face);

    }

    public void hideLayout() {
        mRlFace.setVisibility(View.GONE);

    }

    private void initData() {
        mKeyboardHelper = new SoftKeyboardStateHelper(((Activity) (((ContextWrapper) getContext()).getBaseContext()))
                .getWindow().getDecorView());
        mKeyboardHelper.addSoftKeyboardStateListener(this);
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        hideLayout();
        ZLog.e("onSoftKeyboardOpened" + keyboardHeightInPx);
    }

    @Override
    public void onSoftKeyboardClosed() {
        ZLog.e("onSoftKeyboardClosed");
        showLayout();

    }

    private void showLayout() {
        hideKeyboard(((ContextWrapper) getContext()).getBaseContext());
        // 延迟一会，让键盘先隐藏再显示表情键盘，否则会有一瞬间表情键盘和软键盘同时显示
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mRlFace.setVisibility(View.VISIBLE);
            }
        }, 50);

    }


    /**
     * 隐藏软键盘
     */
    public void hideKeyboard(Context context) {
        Activity activity = (Activity) context;
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive() && activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }
    }

}
