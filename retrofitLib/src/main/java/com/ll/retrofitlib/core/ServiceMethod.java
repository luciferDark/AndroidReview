package com.ll.retrofitlib.core;

import com.ll.retrofitlib.annotations.Field;
import com.ll.retrofitlib.annotations.Get;
import com.ll.retrofitlib.annotations.Post;
import com.ll.retrofitlib.annotations.Query;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;

public class ServiceMethod<T> {
    private final EasyRetrofit easyRetrofit;
    private final Method method;
    private String httpMethod = "";
//    private Annotation[] methodAnotations;
//    private Annotation[][] paramsAnotations;

    private ParamsHandler[] paramsHandlers = null;

    private boolean isPost = false;
    private String relativeUrl = "";

    private HttpUrl baseUrl;
    private Call.Factory factory;
    private HttpUrl.Builder urlBuilder;
    private FormBody.Builder formBodyBuild = null;

    private ServiceMethod(Builder builder) {
        this.easyRetrofit = builder.easyRetrofit;
        this.method = builder.method;
        this.httpMethod = builder.httpMethod;
//        this.methodAnotations = builder.methodAnotations;
//        this.paramsAnotations = builder.paramsAnotations;
        this.paramsHandlers = builder.paramsHandlers;
        this.isPost = builder.isPost;
        this.relativeUrl = builder.relativeUrl;

        this.baseUrl = this.easyRetrofit.baseUrl;
        this.factory = this.easyRetrofit.factory;


        if (isPost) {
            formBodyBuild = new FormBody.Builder();
        }
    }

    public Object invoke(Object[] params) {
        if (paramsHandlers.length != params.length) {
            throw new RuntimeException("params and annotation length did not equals");
        }

        //获取最终请求地址
        HttpUrl url;
        if (urlBuilder == null) {
            urlBuilder = baseUrl.newBuilder(relativeUrl);
        }
        url = urlBuilder.build();
        for (int i = 0; i < params.length; i++) {
            paramsHandlers[i].apply(this, params[i]);
        }

        FormBody formBody = null;
        if (isPost && null != formBodyBuild) {
            formBody = formBodyBuild.build();
        }
        Request request = new Request.Builder()
                .url(url)
                .method(httpMethod, formBody)
                .build();
        return factory.newCall(request);
    }

    public <T> void addQueryParamHandler(String key, T value) {
        if (urlBuilder != null) {
            urlBuilder.addQueryParameter(key, (String) value);
        }
    }

    public <T> void addFieldParamHandler(String key, T value) {
        if (formBodyBuild != null) {
            formBodyBuild.add(key, (String) value);
        }
    }


    public static final class Builder<T> {
        private final EasyRetrofit easyRetrofit;
        private final Method method;
        private Annotation[] methodAnotations;
        private Annotation[][] paramsAnotations;
        private ParamsHandler<T>[] paramsHandlers;

        private boolean isPost = false;
        private String httpMethod = "";
        private String relativeUrl = "";

        public Builder(EasyRetrofit easyRetrofit, Method method) {
            this.easyRetrofit = easyRetrofit;
            this.method = method;

            methodAnotations = method.getAnnotations();
            paramsAnotations = method.getParameterAnnotations();
        }

        public ServiceMethod build() {
            onHandlerMethodAnnotations();

            onHandlerParamsAnnotations();

            return new ServiceMethod(this);
        }

        private void onHandlerMethodAnnotations() {
            boolean hasGetAnnotation = false;
            boolean hasPostAnnotation = false;
            for (Annotation methodAnotation : methodAnotations) {
                if (methodAnotation instanceof Get) {
                    httpMethod = "GET";
                    isPost = false;
                    hasGetAnnotation = true;
                    relativeUrl = ((Get) methodAnotation).value();
                } else if (methodAnotation instanceof Post) {
                    isPost = true;
                    httpMethod = "POST";
                    hasPostAnnotation = true;
                    relativeUrl = ((Post) methodAnotation).value();
                }
            }

            if (hasGetAnnotation && hasPostAnnotation) {
                throw new RuntimeException("method :" + method.getName() + " has both Get and Post annotation.");
            }
        }

        private void onHandlerParamsAnnotations() {
            paramsHandlers = new ParamsHandler[paramsAnotations.length];
            for (int i = 0; i < paramsAnotations.length; i++) {
                //获取到某一个参数的所有注解
                for (Annotation annotation : paramsAnotations[i]) {
                    //处理这个参数中的一个注解
                    if (annotation instanceof Query) {
                        if (isPost)
                            throw new RuntimeException("Post require must use Field annotation,but get Query");
                        //Get 请求参数的key
                        paramsHandlers[i] = new ParamsHandler.Field<>(((Query) annotation).value());
                    } else if (annotation instanceof Field) {
                        if (!isPost)
                            throw new RuntimeException("Get require must use Query annotation,but get Field");
                        //Post请求参数的key
                        paramsHandlers[i] = new ParamsHandler.Query<>(((Field) annotation).value());
                    }
                }
            }
        }
    }
}
