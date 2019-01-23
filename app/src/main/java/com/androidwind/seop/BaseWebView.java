package com.androidwind.seop;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.SSLCertificateSocketFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.androidwind.seop.util.WebViewUtil;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class BaseWebView extends WebView {

    private final static String TAG = "BaseWebView";

    private static final String UA = "androidwind";

    private WebViewListener mWebViewListener;

    public BaseWebView(Context context) {
        super(context);
        init(context);
    }

    public BaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * init webview
     */
    private void init(Context context) {
        File cacheDir = WebViewUtil.getWebViewCacheDir(context);
        String cachePath = cacheDir.getPath();

        WebSettings webSetting = this.getSettings();
        if (webSetting != null) {
            try {
                webSetting.setDatabaseEnabled(true);
                webSetting.setBuiltInZoomControls(false);
                webSetting.setSupportZoom(false);
                webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
                webSetting.setUseWideViewPort(true);
                webSetting.setLoadWithOverviewMode(true);
                webSetting.setJavaScriptEnabled(true);
                webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
                webSetting.setGeolocationEnabled(true);
                webSetting.setAllowFileAccess(false);
                webSetting.setSavePassword(false);

                webSetting.setDomStorageEnabled(true);
                webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
                webSetting.setAppCacheEnabled(true);
                webSetting.setAppCachePath(cachePath);
                webSetting.setDatabasePath(cachePath);
                webSetting.setUserAgentString(webSetting.getUserAgentString() + " " + UA);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    webSetting.setSafeBrowsingEnabled(false);
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        removeJavascriptInterface("searchBoxJavaBridge_");
        removeJavascriptInterface("accessibility");
        removeJavascriptInterface("accessibilityTraversal");
        setInitialScale(100);
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        setHorizontalScrollBarEnabled(false);
        setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    requestFocus();
                }
                if (mWebViewListener != null) {
                    mWebViewListener.onProgressChanged(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (mWebViewListener != null) {
                    mWebViewListener.onReceivedTitle(title);
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     final JsResult result) {
                AlertDialog.Builder b2 = new AlertDialog.Builder(
                        getContext())
                        .setTitle("Tile")
                        .setMessage(message)
                        .setPositiveButton("OK",
                                new AlertDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        result.confirm();
                                    }
                                });

                b2.setCancelable(false);
                b2.create();
                b2.show();
                return true;
            }
        });

        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (mWebViewListener != null) {
                    mWebViewListener.onPageStarted(url);
                }
                startTimeoutTimer();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mWebViewListener != null) {
                    mWebViewListener.onPageFinished(url);
                }
                stopTimeoutTimer();
            }

            @Override
            @RequiresApi(24)
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.d(TAG, "shouldOverrideUrlLoading req url:" + request.getUrl());
                if (null != request.getUrl()) {
                    return this.shouldOverrideUrlLoading(view, request.getUrl().toString());
                } else {
                    return super.shouldOverrideUrlLoading(view, request);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "shouldOverrideUrlLoading url:" + url);
                if (!WebViewUtil.isNetworkUri(Uri.parse(url))) {
                    return false;
                }

                loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.d(TAG, "onReceivedSslError: " + error.getPrimaryError());
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                String info = String.format("onReceivedError code:%d desc:%s url:%s", errorCode, description, failingUrl);
                Log.e(TAG, info);
                loadUrl("file:///android_asset/error.html");
                requestFocus();
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                Log.d(TAG, "onLoadResource");
                if (view.getProgress() == 100) {
                    if (mWebViewListener != null) {
                        mWebViewListener.onPageFinished(url);
                    }
                }
            }

            @Override
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String scheme = request.getUrl().getScheme().trim();
                String method = request.getMethod();
                Map<String, String> headerFields = request.getRequestHeaders();
                String url = request.getUrl().toString();
                if ((scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                        && method.equalsIgnoreCase("get")) {
                    try {
                        URLConnection connection = recursiveRequest(url, headerFields);

                        if (connection == null) {
                            return super.shouldInterceptRequest(view, request);
                        }

                        String contentType = connection.getContentType();
                        String mime = getMime(contentType);
                        String charset = getCharset(contentType);
                        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
                        int statusCode = httpURLConnection.getResponseCode();
                        String response = httpURLConnection.getResponseMessage();
                        Map<String, List<String>> headers = httpURLConnection.getHeaderFields();
                        Set<String> headerKeySet = headers.keySet();

                        if (TextUtils.isEmpty(mime)) {
                            return super.shouldInterceptRequest(view, request);
                        } else {
                            if (!TextUtils.isEmpty(charset) || (isBinaryRes(mime))) {
                                WebResourceResponse resourceResponse = new WebResourceResponse(mime, charset, httpURLConnection.getInputStream());
                                resourceResponse.setStatusCodeAndReasonPhrase(statusCode, response);
                                Map<String, String> responseHeader = new HashMap<String, String>();
                                for (String key : headerKeySet) {
                                    responseHeader.put(key, httpURLConnection.getHeaderField(key));
                                }
                                resourceResponse.setResponseHeaders(responseHeader);
                                return resourceResponse;
                            } else {
                                return super.shouldInterceptRequest(view, request);
                            }
                        }
                    } catch (MalformedURLException e) {
                        Log.e(TAG, "shouldInterceptRequest MalformedURLException");
                        e.printStackTrace();
                    } catch (Exception e) {
                        Log.e(TAG, "shouldInterceptRequest Exception");
                        e.printStackTrace();
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }
        });
    }

    public void setWebChromeClient(WebChromeClient webChromeClient) {
        super.setWebChromeClient(webChromeClient);
    }

    public void setWebViewListener(WebViewListener listener) {
        mWebViewListener = listener;
    }

    private Handler mHandler;
    public static long TIME_OUT = 60000;

    public void startTimeoutTimer() {
        if (null == mHandler) {
            mHandler = new Handler();
        }

        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (BaseWebView.this.getProgress() < 100) {
                    Log.d(TAG, "loading time out!");
//                    NorToast.error("加载超时");
                    if (mWebViewListener != null) {
                        mWebViewListener.onPageTimeout();
                    }
                }
            }
        }, TIME_OUT);
    }

    public void stopTimeoutTimer() {
        try {
            if (null != mHandler) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private Timer countDownTimer;

    public void startTimer(int time) {
        countDownTimer = new Timer();
        countDownTimer.schedule(new TimerTask() {
            public void run() {
                //TODO
            }
        }, time);
    }

    private void releaseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public interface WebViewListener {
        void onPageStarted(String url);

        void onPageFinished(String url);

        void onProgressChanged(int newProgress);

        void onReceivedTitle(String text);

        void onPageTimeout();
    }

    private String getMime(String contentType) {
        if (contentType == null) {
            return null;
        }
        return contentType.split(";")[0];
    }

    private String getCharset(String contentType) {
        if (contentType == null) {
            return null;
        }

        String[] fields = contentType.split(";");
        if (fields.length <= 1) {
            return null;
        }

        String charset = fields[1];
        if (!charset.contains("=")) {
            return null;
        }
        charset = charset.substring(charset.indexOf("=") + 1);
        return charset;
    }

    private boolean isBinaryRes(String mime) {
        if (mime.startsWith("image")
                || mime.startsWith("audio")
                || mime.startsWith("video")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean containCookie(Map<String, String> headers) {
        for (Map.Entry<String, String> headerField : headers.entrySet()) {
            if (headerField.getKey().contains("Cookie")) {
                return true;
            }
        }
        return false;
    }

    private static URLConnection recursiveRequest(String path, Map<String, String> headers) {
        HttpURLConnection conn;
        URL url = null;
        try {
            url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            String[] ips = {""};
            if (ips != null && ips.length > 0) {
                String ip = ips[0];
                String newUrl = path.replaceFirst(url.getHost(), ip);
                conn = (HttpURLConnection) new URL(newUrl).openConnection();

                if (headers != null) {
                    for (Map.Entry<String, String> field : headers.entrySet()) {
                        conn.setRequestProperty(field.getKey(), field.getValue());
                    }
                }
                conn.setRequestProperty("Host", url.getHost());
            } else {
                return null;
            }
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(false);
            if (conn instanceof HttpsURLConnection) {
                final HttpsURLConnection httpsURLConnection = (HttpsURLConnection) conn;
                WebviewTlsSniSocketFactory sslSocketFactory = new WebviewTlsSniSocketFactory((HttpsURLConnection) conn);
                httpsURLConnection.setSSLSocketFactory(sslSocketFactory);
                httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        String host = httpsURLConnection.getRequestProperty("Host");
                        if (null == host) {
                            host = httpsURLConnection.getURL().getHost();
                        }
                        return HttpsURLConnection.getDefaultHostnameVerifier().verify(host, session);
                    }
                });
            }
            int code = conn.getResponseCode();// network block
            if (needRedirect(code)) {
                if (containCookie(headers)) {
                    return null;
                }

                String location = conn.getHeaderField("Location");
                if (location == null) {
                    location = conn.getHeaderField("location");
                }

                if (location != null) {
                    if (!(location.startsWith("http://") || location
                            .startsWith("https://"))) {
                        URL originalUrl = new URL(path);
                        location = originalUrl.getProtocol() + "://"
                                + originalUrl.getHost() + location;
                    }
                    return recursiveRequest(location, headers);
                } else {
                    return null;
                }
            } else {
                return conn;
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "recursiveRequest MalformedURLException");
        } catch (IOException e) {
            Log.e(TAG, "recursiveRequest IOException");
        } catch (Exception e) {
            Log.e(TAG, "unknow exception");
        }
        return null;
    }

    public static boolean needRedirect(int code) {
        return code >= 300 && code < 400;
    }

    static class WebviewTlsSniSocketFactory extends SSLSocketFactory {
        private final String TAG = WebviewTlsSniSocketFactory.class.getSimpleName();
        HostnameVerifier hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
        private HttpsURLConnection conn;

        public WebviewTlsSniSocketFactory(HttpsURLConnection conn) {
            this.conn = conn;
        }

        public void relese() {
            hostnameVerifier = null;
            conn.setHostnameVerifier(null);
            conn.disconnect();
        }

        @Override
        public Socket createSocket() throws IOException {
            return null;
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return null;
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
            return null;
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return null;
        }

        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            return null;
        }

        // TLS layer

        @Override
        public String[] getDefaultCipherSuites() {
            return new String[0];
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return new String[0];
        }

        @Override
        public Socket createSocket(Socket plainSocket, String host, int port, boolean autoClose) throws IOException {
            String peerHost = this.conn.getRequestProperty("Host");
            if (peerHost == null) {
                peerHost = host;
            }
            InetAddress address = plainSocket.getInetAddress();
            if (autoClose) {
                // we don't need the plainSocket
                plainSocket.close();
            }
            // create and connect SSL socket, but don't do hostname/certificate verification yet
            SSLCertificateSocketFactory sslSocketFactory = (SSLCertificateSocketFactory) SSLCertificateSocketFactory.getDefault(0);
            SSLSocket ssl = (SSLSocket) sslSocketFactory.createSocket(address, port);

            // enable TLSv1.1/1.2 if available
            ssl.setEnabledProtocols(ssl.getSupportedProtocols());

            // set up SNI before the handshake
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                sslSocketFactory.setHostname(ssl, peerHost);
            } else {
                try {
                    java.lang.reflect.Method setHostnameMethod = ssl.getClass().getMethod("setHostname", String.class);
                    setHostnameMethod.invoke(ssl, peerHost);
                } catch (Exception e) {
                    Log.e(TAG, "SNI not useable", e);
                }
            }

            // verify hostname and certificate
            SSLSession session = ssl.getSession();

            if (!hostnameVerifier.verify(peerHost, session)) {
                throw new SSLPeerUnverifiedException("Cannot verify hostname: " + peerHost);
            }

            return ssl;
        }
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy");
        stopTimeoutTimer();
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }

        releaseTimer();

        // in android 5.1(sdk:21) we should invoke this to avoid memory leak
        // see (https://coolpers.github.io/webview/memory/leak/2015/07/16/
        // android-5.1-webview-memory-leak.html)
        ViewParent parent = getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(this);
        }
        removeAllViews();
        stopLoading();
        if (null != getSettings()) {
            getSettings().setJavaScriptEnabled(false);
        }
        clearHistory();

        try {
            super.destroy();
        } catch (Throwable e) {
            Log.e(TAG, "destroy exception", e);
        }
    }
}
