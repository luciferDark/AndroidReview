package com.ll.retrofitlib.core;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class EasyRetrofit {
    private final Map<Method, ServiceMethod> serviceMethodCache
            = new ConcurrentHashMap<>();
    final okhttp3.Call.Factory factory;
    final HttpUrl baseUrl;

    private EasyRetrofit(HttpUrl baseUrl, Call.Factory factory) {
        this.baseUrl = baseUrl;
        this.factory = factory;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> services) {
        return (T) Proxy.newProxyInstance(
                services.getClassLoader(),
                new Class[]{services},
                (proxy, method, params) -> {
                    ServiceMethod serviceMethod = paserServiceMethod(method);
                    return serviceMethod.invoke(params);
                });
    }

    private ServiceMethod paserServiceMethod(Method method) {
        ServiceMethod serviceMethod = serviceMethodCache.get(method);
        if (null == serviceMethod) {
            synchronized (serviceMethodCache) {
                if (null == serviceMethod) {
                    serviceMethod = new ServiceMethod.Builder(this, method).build();
                }
            }
        }
        return serviceMethod;
    }

    public static final class Builder {
        private HttpUrl baseUrl;
        private Call.Factory factory;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = HttpUrl.get(baseUrl);
            return this;
        }

        public Builder client(Call.Factory factory) {
            this.factory = factory;
            return this;
        }

        public EasyRetrofit build() {
            if (null == baseUrl) {
                throw new RuntimeException("base url is required, but get null");
            }

            if (null == factory) {
                factory = new OkHttpClient();
            }

            return new EasyRetrofit(baseUrl, factory);
        }
    }
}
