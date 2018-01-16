package com.kuouweather.android.tools;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/1/16 0016.
 */

public abstract class BaseActivity extends Activity implements View.OnClickListener {

    protected String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setListener();
    }

    protected abstract void initView();

    protected abstract void setListener();

    protected void ToastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
