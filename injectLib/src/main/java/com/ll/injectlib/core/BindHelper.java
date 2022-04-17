package com.ll.injectlib.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ll.injectlib.annotations.Autowired;
import com.ll.injectlib.annotations.BindView;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

public class BindHelper {
    private static BindHelper _instance = null;

    private BindHelper() {
    }

    public static BindHelper Instance() {
        if (null == _instance) {
            synchronized (BindHelper.class) {
                if (null == _instance) {
                    _instance = new BindHelper();
                }
            }
        }

        return _instance;
    }

    /**
     * 自动findViewById
     *
     * @param activity
     */
    public void BindViewId(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        Class<? extends BindView> annotationClazz = BindView.class;

        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            // 成员变量打上了BindView注解
            if (field.isAnnotationPresent(annotationClazz)) {
                BindView annotation = field.getAnnotation(annotationClazz);
                int viewId = annotation != null ? annotation.value() : -1;
                Log.d("==", viewId + "");
                if (-1 == viewId) continue;

                View view = activity.findViewById(viewId);
                try {
                    field.setAccessible(true);
                    field.set(activity, view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void IntentValueAutowired(Activity activity) {
        Intent intent = activity.getIntent();
        Bundle extras = intent.getExtras();
        if (null == extras) {
            //没有数据传递
            return;
        }
        Class<? extends Activity> clazz = activity.getClass();
        Class<? extends Autowired> annotationClazz = Autowired.class;

        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            // 成员变量打上了BindView注解
            if (!field.isAnnotationPresent(annotationClazz)) {
                continue;
            }
            Autowired annotation = field.getAnnotation(annotationClazz);
            String key = annotation != null ? annotation.value().trim() : null;
            if (TextUtils.isEmpty(key)) {
                key = field.getName();
            }
            Object obj = extras.get(key);

            //这边对 Parcelable 序列化数组类型做特殊处理
            if (field.getType().isArray() &&
                    Parcelable.class.isAssignableFrom(
                            Objects.requireNonNull(field.getType().getComponentType()))) {
                Object[] objects = (Object[]) obj;

                obj = Arrays.copyOf(
                        objects, objects.length, (Class<? extends Object[]>) field.getType());
            }
            try {
                field.setAccessible(true);
                field.set(activity, obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
