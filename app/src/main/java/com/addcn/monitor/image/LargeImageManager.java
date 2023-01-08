package com.addcn.monitor.image;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.addcn.monitor.util.ConvertUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;

/**
 * @author Gentlman
 * @time 2022/12/5 15:10
 * @desc
 */
public class LargeImageManager {

    /**
     * 文件大小阈值 kb为单位
     */
    private double fileSizeThreshold = 500.0;
    /**
     * 内存大小阈值 kb为单位
     */
    private double memorySizeThreshold = 800.0;

    /**
     * Okhttp应用拦截器
     */
    private List<Interceptor> okHttpInterceptors = new ArrayList<>();
    /**
     * Okhttp网络拦截器
     */
    private List<Interceptor> okHttpNetworkInterceptors = new ArrayList<>();

    /**
     * 保存大图信息
     */
    public static Map<String, LargeImageInfo> LARGE_IMAGE_INFO_MAP = new HashMap<>();


    private LargeImageManager() {

    }

    private static class Holder {
        private static final LargeImageManager instance = new LargeImageManager();
    }

    public static LargeImageManager getInstance() {
        return Holder.instance;
    }

    /**
     * 设置文件阈值 单位为kb
     *
     * @param fileSizeThreshold 文件阈值 单位kb
     * @return
     */
    public LargeImageManager setFileSizeThreshold(double fileSizeThreshold) {
        this.fileSizeThreshold = fileSizeThreshold;
        return this;
    }

    /**
     * 设置内存阈值 单位为kb
     *
     * @param memorySizeThreshold 内存阈值 单位为kb
     * @return
     */
    public LargeImageManager setMemorySizeThreshold(double memorySizeThreshold) {
        this.memorySizeThreshold = memorySizeThreshold;
        return this;
    }

    /**
     * 因为实现了Okhttp的全局插桩，所以提供一个可以添加拦截器的方法
     * 让用户可以自定义拦截器实现自己项目和三方库的OKhttp全局监听
     *
     * @param interceptor 应用拦截器
     */
    public LargeImageManager addOkHttpInterceptor(Interceptor interceptor) {
        if (null != okHttpInterceptors) {
            okHttpInterceptors.add(interceptor);
        }
        return this;
    }

    /**
     * 添加Okhttp网络拦截器
     *
     * @param networkInterceptor 网络拦截器
     */
    public LargeImageManager addOkHttpNetworkInterceptor(Interceptor networkInterceptor) {
        if (null != okHttpNetworkInterceptors) {
            okHttpNetworkInterceptors.add(networkInterceptor);
        }
        return this;
    }

    public List<Interceptor> getOkHttpInterceptors() {
        return okHttpInterceptors;
    }

    public List<Interceptor> getOkHttpNetworkInterceptors() {
        return okHttpNetworkInterceptors;
    }

    public double getFileSizeThreshold() {

        return fileSizeThreshold;
    }

    public double getMemorySizeThreshold() {
        return memorySizeThreshold;
    }


    public void process(String url, int size) {
        //转化成kb
        double fileSize = size / 1024.0;
        saveImageInfo(url, fileSize);
    }


    /**
     * 保存网络图片信息
     *
     * @param url
     * @param fileSize
     */
    private void saveImageInfo(String url, double fileSize) {
        LargeImageInfo largeImageInfo;
        if (LARGE_IMAGE_INFO_MAP.containsKey(url)) {
            largeImageInfo = LARGE_IMAGE_INFO_MAP.get(url);
        } else {
            largeImageInfo = new LargeImageInfo();
            LARGE_IMAGE_INFO_MAP.put(url, largeImageInfo);
            largeImageInfo.setUrl(url);
        }
        largeImageInfo.setFileSize(fileSize);
    }

    /**
     * 保存在内部里的图片信息
     *
     * @param url
     * @param memorySize
     * @param width
     * @param height
     */
    public void saveImageInfo(String url, double memorySize, int width, int height, String framework) {
        LargeImageInfo largeImageInfo;
        if (LARGE_IMAGE_INFO_MAP.containsKey(url)) {
            largeImageInfo = LARGE_IMAGE_INFO_MAP.get(url);
        } else {
            largeImageInfo = new LargeImageInfo();
            LARGE_IMAGE_INFO_MAP.put(url, largeImageInfo);
            largeImageInfo.setUrl(url);
        }
        largeImageInfo.setMemorySize(memorySize);
        largeImageInfo.setWidth(width);
        largeImageInfo.setHeight(height);
        largeImageInfo.setFramework(framework);
    }


}
