package com.androidwind.seop;


import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class TinyWebView extends BaseWebView {

    private final static String TAG = "TinyWebView";

    /**
     * javascriptinterface support
     */
    public TinyWebView(Builder builder) {
        super(builder.getContext());
    }

    /**
     * init new webview
     */
    public static Builder with(@NonNull Context context) {
        if (context == null) {
            throw new NullPointerException("activity can not be null .");
        }
        return new Builder(context);
    }

    public static final class Builder {
        private Context context;

        private TinyWebView tinyWebView;

        private String url;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public TinyWebView create() {
            tinyWebView = new TinyWebView(this);
            //handle url
            if (!TextUtils.isEmpty(url)) {
                tinyWebView.loadUrl(url);
            }
            return tinyWebView;
        }

        public Builder go(String url) {
            this.url = url;
            return this;
        }
    }

}
