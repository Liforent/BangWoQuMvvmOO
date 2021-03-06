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
                        //??????crash (???????????????????????????????????????scheme?????????url???APP, ?????????crash)
                        //???????????????app????????????true?????????????????????????????????????????????????????????????????????????????????
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
        Log.i("ansen", "????????????????????????:" + webView.canGoBack());
        if (webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {//???????????????????????????????????????????????????
            webView.goBack(); // goBack()????????????webView???????????????
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }


    public void destroy() {
        if (webView != null) {
            // ???????????????destroy()?????????????????????if (isDestroyed()) return;???????????????????????????onDetachedFromWindow()??????
            // destory()
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }
            webView.stopLoading();
            // ????????????????????????????????????????????????????????????????????????????????????
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


    //????????????activity?????????????????????????????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INSTALL_APK) {
            finish();
        }
    }



}


