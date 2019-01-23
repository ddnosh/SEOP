package com.androidwind.seop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.widget.FrameLayout;

import com.androidwind.seop.ui.BaseActivity;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.StringUtils;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class WebPageActivity extends BaseActivity {

    protected static String TAG = "WebPageActivity";

    private static final String URL = "http://www.androidwind.com";
    protected FrameLayout mWebViewContainer;
    private TinyWebView mTinyWebView;

    public static void newInstance(Context context, String url) {
        Intent intent = new Intent();
        intent.setClass(context, WebPageActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webpage);

        String url = getIntent().getStringExtra("url");
        if (StringUtils.isEmpty(url)) {
            url = URL;
        }

        initWebView(url);
    }

    private void initWebView(String url) {
        mWebViewContainer = findViewById(R.id.webview_container);
        mTinyWebView = TinyWebView.with(this).go(url).create();
        if (mTinyWebView != null) {
            mWebViewContainer.addView(mTinyWebView);
        }

        if (NetworkUtils.isConnected()) {
            mTinyWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        } else {
            mTinyWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        }
    }

    @Override
    protected void onDestroy() {
        if (mTinyWebView != null) {
            mTinyWebView.destroy();
        }
        super.onDestroy();
    }
}
