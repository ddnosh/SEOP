package com.androidwind.seop.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class BaseActivity extends AppCompatActivity {

    protected Intent getGoIntent(Class<?> clazz) {
        if (BaseFragment.class.isAssignableFrom(clazz)) {
            Intent intent = new Intent(this, FrameActivity.class);
            intent.putExtra("fragmentName", clazz.getName());
            return intent;
        } else {
            return new Intent(this, clazz);
        }
    }

    protected void readyGo(Class<?> clazz) {
        Intent intent = getGoIntent(clazz);
        startActivity(intent);
    }
}
