package com.addcn.monitor.record;

import android.util.Log;

import com.addcn.monitor.annotation.NoMethodRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoMethodRecord
public class MethodCallFilterRecorder {
    private final Map<String, MethodBean> methodMap = new ConcurrentHashMap<>();

    public static void filter(MethodCallFilterRecorder recorder, int maxCallCount) {
        recorder.printMaxCallCountMethod(maxCallCount);
    }

    public void recordMethod(MethodRecordMessageData data) {
        String name = data.getClassName() + data.getMethodName() + data.getDesc();
        MethodBean bean = this.methodMap.get(name);
        if (bean != null) {
            MethodBean.addCallCount(bean);
        } else {
            this.methodMap.put(name, new MethodBean());
        }
    }

    private List<MethodBean> sortMethod() {
        List<MethodBean> list = new ArrayList<>();
        for (Map.Entry<String, MethodBean> entry : this.methodMap.entrySet()) {
            entry.getValue().setMethodKey(entry.getKey());
            list.add(entry.getValue());
        }
        Collections.sort(list);
        return list;
    }

    public void printMaxCallCountMethod(int maxCallCount) {
        for (MethodBean bean : sortMethod()) {
            if (MethodBean.getCallCount(bean) > (maxCallCount << 1)) {
                Log.d("CallFilterRecorder", bean.getMethodKey() + ":" + MethodBean.getCallCount(bean));
            }
        }
    }

    public static void debugMethodCallCount(List<List<MethodRecordMessageData>> list, int maxCallCount) {
        new Thread((Runnable) new MethodRunnable(list, maxCallCount)).start();
    }

    @NoMethodRecord
    static class MethodBean implements Comparable<MethodBean> {
        private int callCount = 0;
        private String methodKey;


        @Override
        public int compareTo(MethodBean methodBean) {
            return compare(methodBean);
        }


        public int compare(MethodBean bean) {
            return bean.callCount - this.callCount;
        }


        static int getCallCount(MethodBean bean) {
            return bean.callCount;
        }

        static int addCallCount(MethodBean bean) {
            int i = bean.callCount + 1;
            bean.callCount = i;
            return i;
        }

        public String getMethodKey() {
            return methodKey;
        }

        public void setMethodKey(String mo) {
            this.methodKey = mo;
        }
    }


    @NoMethodRecord
    static final class MethodRunnable implements Runnable {
        final int maxCallCount;
        final List<List<MethodRecordMessageData>> list;

        MethodRunnable(List<List<MethodRecordMessageData>> list, int maxCallCount) {
            this.list = list;
            this.maxCallCount = maxCallCount;
        }

        @Override
        public void run() {
            MethodCallFilterRecorder recorder = new MethodCallFilterRecorder();
            for (List<MethodRecordMessageData> list : list) {
                for (MethodRecordMessageData data : list) {
                    recorder.recordMethod(data);
                }
            }
            MethodCallFilterRecorder.filter(recorder, this.maxCallCount);
        }
    }
}
