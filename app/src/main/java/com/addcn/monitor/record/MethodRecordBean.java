package com.addcn.monitor.record;

import com.addcn.monitor.annotation.NoMethodRecord;

import java.util.ArrayList;
import java.util.List;

@NoMethodRecord
public class MethodRecordBean {
    private List<String> children;
    private final String className;
    private final String desc;
    private long endTime;
    private final String id;
    private final String methodName;
    private final int depth;
    private final String parentId;
    private final long startTime;


    public MethodRecordBean(String id, String parentId, MethodRecordMessageData messageData, int depth) {
        this.id = id;
        this.parentId = parentId;
        this.className = messageData.getClassName();
        this.methodName = messageData.getMethodName();
        this.desc = messageData.getDesc();
        this.depth = depth;
        this.startTime = messageData.getTime();
    }

    public long getDuration() {
        return this.endTime - this.startTime;
    }

    public String getParentId() {
        return this.parentId;
    }

    public int getDepth() {
        return this.depth;
    }


    public List<String> getChildren() {
        return this.children;
    }

    public String getClassName() {
        return this.className;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getId() {
        return this.id;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public String getThreadName() {
        return this.id.split("#")[0];
    }

    public boolean verification(MethodRecordMessageData messageData) {
        return this.className.equals(messageData.getClassName())
                && this.methodName.equals(messageData.getMethodName())
                && this.desc.equals(messageData.getDesc());
    }

    public void addChild(String string) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(string);
    }

    public void update(long time) {
        this.endTime = time;
    }

    @Override
    public String toString() {
        return "MethodRecordBean{" +
                "children=" + children +
                ", className='" + className + '\'' +
                ", desc='" + desc + '\'' +
                ", endTime=" + endTime +
                ", id='" + id + '\'' +
                ", methodName='" + methodName + '\'' +
                ", depth=" + depth +
                ", parentId='" + parentId + '\'' +
                ", startTime=" + startTime +
                '}';
    }
}
