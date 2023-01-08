package com.addcn.monitor.record;

import com.addcn.monitor.annotation.NoMethodRecord;

@NoMethodRecord
public class MethodRecordMessageData {
    private final String className;
    private final String methodName;
    private final String desc;
    private final boolean isStart;

    private String threadName;
    private long time;

    public MethodRecordMessageData(String className, String methodName, String desc, long time, boolean isStart) {
        this.className = className;
        this.methodName = methodName;
        this.desc = desc;
        this.time = time;
        this.isStart = isStart;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getThreadName() {
        return threadName;
    }

    public boolean isStart() {
        return this.isStart;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getDesc() {
        return desc;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "MethodRecordMessageData{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", desc='" + desc + '\'' +
                ", isStart=" + isStart +
                ", threadName='" + threadName + '\'' +
                ", time=" + time +
                '}';
    }
}
