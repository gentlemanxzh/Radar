package com.addcn.monitor.record;

import android.os.Looper;

import com.addcn.monitor.annotation.NoMethodRecord;

@NoMethodRecord
public class MainThreadMethodInterceptor implements IMethodRecordInterceptor {
    private static final Looper mainLooper = Looper.getMainLooper();
    @Override
    public boolean onMethodIntercept(String className, String methodName, String desc) {
        return Looper.myLooper() == null || Looper.myLooper() != mainLooper;
    }
}
