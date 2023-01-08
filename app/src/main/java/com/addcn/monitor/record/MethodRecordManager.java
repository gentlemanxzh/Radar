package com.addcn.monitor.record;

import android.util.Log;

import com.addcn.monitor.annotation.NoMethodRecord;

import java.util.Map;

@NoMethodRecord
public class MethodRecordManager {

    private static volatile boolean isRunning = true;
    private static final MethodRecordCollector recordCollector = new MethodRecordCollector();
    private static MethodRecordAggregator recordAggregator;

    private static IMethodRecordInterceptor interceptor = new MainThreadMethodInterceptor();


    public static void setInterceptor(IMethodRecordInterceptor iMethodRecordInterceptor) {
        interceptor = iMethodRecordInterceptor;
    }


    public static void reset() {
        recordCollector.resetRecordList();
        MethodRecordAggregator aggregator = recordAggregator;
        if (aggregator != null) {
            aggregator.reset();
        }
        isRunning = true;
    }

    public static void stop() {
        isRunning = false;
    }

    public static void endMethodRecordWithMethodCallCount(int maxCallCount) {
        stop();
        MethodCallFilterRecorder.debugMethodCallCount(recordCollector.getRecordList(), maxCallCount);
    }


    public static Map<String, IMethodRecorder> getRecorderMap() {
        if (recordAggregator == null) {
            recordAggregator = new MethodRecordAggregator();
        }
        return recordAggregator.getRecorderMap(recordCollector);
    }


    public static void recordMethodStart(String className, String methodName, String desc) {
        if (!isRunning || (interceptor != null && interceptor.onMethodIntercept(className, methodName, desc))) {
            return;
        }
        final MethodRecordMessageData data =
                new MethodRecordMessageData(className, methodName, desc,
                        System.currentTimeMillis(), true);
        recordCollector.collectMethodInfo(data);
    }

    public static void recordMethodEnd(String className, String methodName, String desc) {
        if (!isRunning || (interceptor != null && interceptor.onMethodIntercept(className, methodName, desc))) {
            return;
        }
        final MethodRecordMessageData data =
                new MethodRecordMessageData(className, methodName, desc,
                        System.currentTimeMillis(), false);
        recordCollector.collectMethodInfo(data);
    }

    public static void recordMethodEndPrint(String className, String methodName, String desc) {
        if (!isRunning || (interceptor != null && interceptor.onMethodIntercept(className, methodName, desc))) {
            return;
        }
        final MethodRecordMessageData data =
                new MethodRecordMessageData(className, methodName, desc,
                        System.currentTimeMillis(), false);
        recordCollector.collectMethodInfo(data);
    }
}
