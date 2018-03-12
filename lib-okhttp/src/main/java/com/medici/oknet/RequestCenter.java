package com.medici.oknet;

import com.medici.oknet.listener.DisposeDataHandle;
import com.medici.oknet.listener.DisposeDataListener;
import com.medici.oknet.listener.DisposeDownloadListener;
import com.medici.oknet.request.CommonRequest;
import com.medici.oknet.request.RequestParams;

/**
 * Created by renzhiqiang on 17/4/24.
 *
 * @function 请求发送中心
 */
public class RequestCenter {

    /**
     * 根据参数发送所有post请求
     * @param url
     * @param params
     * @param listener
     * @param clazz
     */
    public static void postRequest(String url, RequestParams params, DisposeDataListener listener, Class<?> clazz) {
        CommonOkHttpClient.get(CommonRequest.createGetRequest(url, params), new DisposeDataHandle(listener, clazz));
    }

    /**
     * 文件下载
     *
     * @param url
     * @param path
     * @param listener
     */
    public static void downloadFile(String url, String path, DisposeDownloadListener listener) {
        CommonOkHttpClient.downloadFile(CommonRequest.createGetRequest(url, null),
                new DisposeDataHandle(listener, path));
    }
}
