package com.addcn.monitor.thread;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Gentlman
 * @time 2022/12/8 16:26
 * @desc
 */
public class CustomThread extends Thread {

    private final static AtomicInteger threadId = new AtomicInteger(0);

    private static String generateThreadName(String name, String className) {
        String threadName = className + "-" + threadId.getAndIncrement();
        if (!TextUtils.isEmpty(name)) {
            threadName += ("-" + name);
        }
        return threadName;
    }

    public CustomThread(String name, String className) {
        this(null, name, className);
    }


    public CustomThread(Runnable target, String className) {
        this(target, null, className);
    }


    public CustomThread(Runnable target, String name, String className) {
        super(target, generateThreadName(name, className));
    }


}
