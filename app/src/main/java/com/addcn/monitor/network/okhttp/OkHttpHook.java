package com.addcn.monitor.network.okhttp;

import android.util.Log;

import com.addcn.monitor.network.okhttp.interceptor.AbsInterceptor;
import com.addcn.monitor.network.okhttp.interceptor.LargeImageInterceptor;
import com.addcn.monitor.util.ReflectUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * @author Gentlman
 * @time 2022/12/5 19:06
 * @desc
 */
public class OkHttpHook {
    /**
     * 添加大图检测的拦截器 通过字节码插入
     */
    public static void addIntercept(OkHttpClient client) {

        try {
            List<Interceptor> interceptors = new ArrayList<>(client.interceptors());
            List<Interceptor> networkInterceptors = new ArrayList<>(client.networkInterceptors());
            noDuplicateAdd(interceptors, new LargeImageInterceptor());
            //需要用反射重新赋值 因为源码中创建了一个不可变的list
            ReflectUtils.reflect(client).field("interceptors", interceptors);
            ReflectUtils.reflect(client).field("networkInterceptors", networkInterceptors);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //list判断是否重复添加
    private static void noDuplicateAdd(List<Interceptor> interceptors, AbsInterceptor interceptor) {
        boolean hasInterceptor = false;
        for (Interceptor i : interceptors) {
            if (i instanceof AbsInterceptor) {
                if (((AbsInterceptor) i).TAG.equals(interceptor.TAG)) {
                    hasInterceptor = true;
                    break;
                }
            }
        }
        if (!hasInterceptor) {
            interceptors.add(interceptor);
        }

    }
}
