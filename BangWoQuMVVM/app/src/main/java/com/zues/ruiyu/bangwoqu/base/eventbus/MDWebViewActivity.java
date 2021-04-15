package com.zues.ruiyu.bangwoqu.base.eventbus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.zues.ruiyu.bangwoqu.R;
import com.zues.ruiyu.bangwoqu.base.BaseActivity;
import com.zues.ruiyu.bangwoqu.custom.ZssTitleView;

import me.jingbin.progress.WebProgress;


public class MDWebViewActivity extends BaseActivity {


    private static final int REQUEST_INSTALL_APK = 5;
    private WebView webView;
    private String url;
    private WebProgress progress;
    private ProgressBar progressBar;
    private boolean isFullScreen;
    private ZssTitleView titleView;
    private static final String TAG = "MDWebViewActivity";
    @Override
    public int getLayoutId() {
        return R.layout.activity_webview;
    }



    public void initView() {

        url = getIntent().getStringExtra("URL");

        titleView = findViewById(R.id.title_view);
        progress = findViewById(R.id.progress);
        progress.setColor("#00F45C52", "#F45C52");
        progressBar = findViewById(R.id.progress_bar);
        webView = findViewById(R.id.web_view);
        titleView.setBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webView.loadUrl(url);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        // webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        WebChromeClient chromeClient = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progress.setWebProgress(newProgress);
                if (newProgress > 80) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        };
        webView.setWebChromeClient(chromeClient);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) {
                    return false;
                } else {
                    try {
                        if (url.startsWith("weixin://")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                            return true;
                        } else if (!url.startsWith("http")) {
                            return true;
                        }
                    } catch (Exception e) {
                        //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                        //没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
                        return false;
                    }
                    return false;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("ansen", "是否有上一个页面:" + webView.canGoBack());
        if (webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {//点击返回按钮的时候判断有没有上一页
            webView.goBack(); // goBack()表示返回webView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }


    public void destroy() {
        if (webView != null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }
            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearHistory();
            webView.clearView();
            webView.removeAllViews();
            try {
                webView.destroy();
            } catch (Throwable ex) {

            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroy();
    }


    //开启一个activity后，返回结果调用的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INSTALL_APK) {
            finish();
        }
    }



}


