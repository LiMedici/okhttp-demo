package com.medici.oknet.okhttp;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @desc OkHttpInterceptor，use set okhttp call header
 * @author cnbilzh
 */
public class OkHttpInterceptor implements Interceptor {

    private Map<String, String> headers;

    public OkHttpInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request().newBuilder();
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            for (String headerKey : keys) {
                builder.addHeader(headerKey,headers.get(headerKey)).build();
            }
        }
        return chain.proceed(builder.build());

    }

}
