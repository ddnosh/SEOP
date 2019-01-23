package com.androidwind.seop.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class WebViewUtil {

    public static File getAppCacheDir(Context context) {
        if (!isExternalStorageEnable()) {
            return context.getFilesDir();
        } else {
            File file = new File(Environment.getExternalStorageDirectory()
                    + "/android-tiny-webview");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file;
        }
    }

    public static File getWebViewCacheDir(Context context) {
        return new File(getAppCacheDir(context), "webcache");
    }

    public static boolean isExternalStorageEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    @Nullable
    public static String getSchemeOrNull(@Nullable Uri uri) {
        return uri == null ? null : uri.getScheme();
    }

    public static final String HTTP_SCHEME = "http";
    public static final String HTTPS_SCHEME = "https";

    public static boolean isNetworkUri(@Nullable Uri uri) {
        final String scheme = getSchemeOrNull(uri);
        return HTTPS_SCHEME.equals(scheme) || HTTP_SCHEME.equals(scheme);
    }

}
