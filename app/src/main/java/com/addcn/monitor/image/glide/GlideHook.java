package com.addcn.monitor.image.glide;

import android.util.Log;

import com.addcn.monitor.util.ReflectUtils;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.SingleRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gentlman
 * @time 2022/12/5 15:08
 * @desc
 */
public class GlideHook {
    public static void process(Object singleRequest){
        Log.d("GlideHook","process");
        try {
            List<RequestListener<?>> requestListeners = null;
            if (singleRequest instanceof SingleRequest) {
                requestListeners = ReflectUtils.reflect(singleRequest).field("requestListeners").get();
            }
            //可能存在用户没有引入okhttp的情况
            if (requestListeners == null) {
                requestListeners = new ArrayList<>();
                requestListeners.add(new GlideLargeImageListener<>());
            } else {
                requestListeners.add(new GlideLargeImageListener<>());
            }
            if (singleRequest instanceof SingleRequest) {
                ReflectUtils.reflect(singleRequest).field("requestListeners",requestListeners);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
