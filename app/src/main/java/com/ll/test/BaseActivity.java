package com.ll.test;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ll.injectlib.core.BindHelper;

public abstract class BaseActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        BindHelper.Instance().BindViewId(this);
        BindHelper.Instance().IntentValueAutowired(this);

        onCreateBase(savedInstanceState);
    }

    public abstract @LayoutRes @NonNull int getLayoutId();

    public abstract void onCreateBase(@Nullable Bundle savedInstanceState);
}
