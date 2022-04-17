package com.ll.test;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ll.injectlib.annotations.Autowired;
import com.ll.injectlib.annotations.BindView;

import java.util.Arrays;

public class SecondActivity extends BaseActivity {
    @BindView(R.id.text2)
    TextView textView;
    @Autowired
    String name;
    @Autowired("age")
    int age;
    @Autowired
    double height;
    @Autowired("family")
    String[] friend;
    @Autowired("parcelableTest")
    ParcelableTest[] tests;

    @NonNull
    @Override
    public int getLayoutId() {
        return R.layout.second_layout;
    }

    @Override
    public void onCreateBase(@Nullable Bundle savedInstanceState) {
        test();
    }

    private void test() {
        StringBuilder builder = new StringBuilder();
        builder.append("Name:").append(name).append("\n")
                .append("Age:").append(age).append("\n")
                .append("Height:").append(height).append("\n")
                .append("Family:").append(Arrays.deepToString(friend)).append("\n")
                .append("ParcelableTest[]:").append(Arrays.deepToString(tests));
        textView.setText(builder.toString());
    }
}
