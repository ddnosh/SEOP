package com.androidwind.seop.ui;

import android.os.Bundle;
import android.util.Log;

import com.androidwind.seop.R;
import com.androidwind.seop.util.ReflectUtil;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class FrameActivity extends BaseActivity {

    protected static String TAG = "FrameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        String className = getIntent().getExtras().getString("fragmentName");
        Log.i(TAG, "the fragment class name is->" + className);
        if (className != null) {
            Object object = ReflectUtil.getObject(className);
            if (object instanceof BaseFragment) {
                BaseFragment fragment = (BaseFragment) object;
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commitAllowingStateLoss();
                }
            } else {
                Log.e(TAG, " the fragment class is not exist!!!");
            }
        }
    }
}
