package com.ll.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ll.injectlib.annotations.BindView;
import com.ll.retrofitlib.core.EasyRetrofit;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements  Callback{
    private static final String TAG = "MainActivity";
    private  Handler handler;
    @BindView(R.id.text)
    public TextView textView;

    @BindView(R.id.btn)
    public Button button;
    @BindView(R.id.btn_get)
    public Button buttonGet;
    @BindView(R.id.btn_post)
    public Button buttonPost;

    EasyRetrofit easyRetrofit;
    WeatherApi api;
    String city = "110101";
    String key = "ae6c53e2186f33bbf240a12d80672d1b";

    @NonNull
    @Override
    public int getLayoutId() {
        return R.layout.main_layout;
    }

    @Override
    public void onCreateBase(@Nullable Bundle savedInstanceState) {
        init();
        test();
    }

    private void init() {
        initEasyRetrofit();
        handler = new Handler();
    }

    private void initEasyRetrofit() {
        String baseUrl = "https://restapi.amap.com";
        easyRetrofit = new EasyRetrofit.Builder().baseUrl(baseUrl).build();
        api = easyRetrofit.create(WeatherApi.class);
    }

    private void test() {
        textView.setText("初始化成功");
        button.setText("跳转第二页面");
        ParcelableTest[] parcelableTests = new ParcelableTest[]{
                new ParcelableTest("林雷", 30),
                new ParcelableTest("王麻子", 24),
                new ParcelableTest("李帅天", 16)
        };

        button.setOnClickListener(view -> {
            Intent intent = new Intent(this, SecondActivity.class);
            intent.putExtra("name", "kylin")
                    .putExtra("age", 12)
                    .putExtra("height", 171.5)
                    .putExtra("family", new String[]{"李磊", "王林", "苏铭"})
                    .putExtra("parcelableTest", parcelableTests)
            ;
            startActivity(intent);
        });

        buttonGet.setOnClickListener(view -> {
           Call call =  api.getWeather(city, key);
           call.enqueue(this);
        });
        buttonPost.setOnClickListener(view -> {
            Call call =  api.postWeather(city, key);
            call.enqueue(this);
        });
    }

    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful()){
            String result = response.body().string();
            Log.d(TAG, "request:" + result);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText(result);
                }
            });
        }
    }
}
