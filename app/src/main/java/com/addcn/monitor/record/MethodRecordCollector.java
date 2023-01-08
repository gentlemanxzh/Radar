package com.addcn.monitor.record;

import com.addcn.monitor.annotation.NoMethodRecord;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@NoMethodRecord
public class MethodRecordCollector implements IMethodRecordCollector {
    private final List<List<MethodRecordMessageData>> mRecordList = new ArrayList<>(10);
    private static final ThreadLocal<ThreadLocalCollector> collectorLocal = new ThreadLocal<>();

    /**
     * 获取 方法收集信息
     *
     * @return List<List < MethodRecordMessageData>>
     */
    @Override
    public List<List<MethodRecordMessageData>> getRecordList() {
        return this.mRecordList;
    }

    /**
     * 收集方法信息
     *
     * @param data MethodRecordMessageData
     */
    @Override
    public void collectMethodInfo(MethodRecordMessageData data) {
        ThreadLocalCollector collector = collectorLocal.get();
        if (collector == null) {
            collector = new ThreadLocalCollector();
            collectorLocal.set(collector);
            synchronized (mRecordList) {
                mRecordList.add(collector.getList());
            }
        }
        data.setThreadName(collector.getThreadName());
        collector.getList().add(data);
    }

    /**
     * 重置信息收集器
     */
    @Override
    public void resetRecordList() {
        for (List<MethodRecordMessageData> methodRecordMessageDataList : mRecordList) {
            methodRecordMessageDataList.clear();
        }
        this.mRecordList.clear();
    }

    @NoMethodRecord
    private static class ThreadLocalCollector {
        private final String threadName;
        private final List<MethodRecordMessageData> list;

        public ThreadLocalCollector() {
            this.threadName = Thread.currentThread().toString();
            this.list = new LinkedList<>();
        }

        public String getThreadName() {
            return threadName;
        }

        public List<MethodRecordMessageData> getList() {
            return list;
        }
    }
}
