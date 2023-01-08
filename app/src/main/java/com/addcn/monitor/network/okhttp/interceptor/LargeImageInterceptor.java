package com.addcn.monitor.network.okhttp.interceptor;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.addcn.monitor.MonitorPluginConfig;
import com.addcn.monitor.image.LargeImageManager;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Gentlman
 * @time 2022/12/5 18:38
 * @desc
 */
public class LargeImageInterceptor extends AbsInterceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (!MonitorPluginConfig.SWITCH_BIG_IMG) {
            return response;
        }
        String contentType = response.header("Content-Type");
        if (isImage(contentType)) {
            processResponse(response);
        }
        return response;
    }

    private void processResponse(Response response) {
        String field = response.header("Content-Length");
        if (!TextUtils.isEmpty(field)) {
            LargeImageManager.getInstance().process(response.request().url().toString(), Integer.parseInt(field));
        }
    }

    /**
     * 判断是否是图片
     */
    private boolean isImage(String contentType) {
        String mimeType = stripContentExtras(contentType);
        return mimeType.startsWith("image");
    }

    private String stripContentExtras(String contentType) {
        int index = contentType.indexOf(';');
        return (index >= 0) ? contentType.substring(0, index) : contentType;
    }

}
