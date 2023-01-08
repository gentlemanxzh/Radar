package com.addcn.monitor.record;

import com.addcn.monitor.annotation.NoMethodRecord;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@NoMethodRecord
public class ThreadMethodRecorder implements IMethodRecorder {
    private final Stack<MethodRecordBean> mMethodStack = new Stack<>();
    private final Map<String, MethodRecordBean> recordBeanMap = new HashMap<>();
    private int mMethodCount = 0;
    private final List<MethodRecordBean> list;
    private final String mThreadName;
    private MethodRecordBean mLastEndBean;

    public ThreadMethodRecorder(String threadName) {
        this.list = new ArrayList<>();
        this.mThreadName = threadName;
    }

    private void recordMethodBean(MethodRecordBean bean, MethodRecordMessageData recordMessageData) {
        bean.update(recordMessageData.getTime());
        this.mLastEndBean = bean;
        this.recordBeanMap.put(bean.getId(), bean);
    }

    @Override
    public boolean debugStackEmpty() {
        return this.mMethodStack.isEmpty();
    }

    @Override
    public Map<String, MethodRecordBean> getMethodRecordMap() {
        return this.recordBeanMap;
    }

    @Override
    public String getRecorderName() {
        return this.mThreadName;
    }

    @Override
    public void recordMethodEnd(MethodRecordMessageData messageData) {
        MethodRecordBean methodStartBean = mMethodStack.pop();
        // 验证是否pop的StartBean与EndBean为同一方法的统计数据
        if (!methodStartBean.verification(messageData)) {
            // 如果上一次的方法结束回调与此回调属于同一方法则表明方法回调了两次结束
            // throw和finally各一次，所以忽略第二次方法结束回调，将数据重新入栈
            if (this.mLastEndBean.verification(messageData)) {
                mMethodStack.push(methodStartBean);
                return;
            }
            // 反之由于方法使用了throws，导致方法不能正常退出
            // 从而`mMethodStack`中push了许多无用startBean
            while (!methodStartBean.verification(messageData)) {
                if (!mMethodStack.isEmpty()) {
                    this.list.add(methodStartBean);
                    System.out.println("recordMethodEnd 丢弃:" + methodStartBean);
                    methodStartBean = mMethodStack.pop();
                }else {
                    throw new EmptyStackException();
                }
            }
        }
        recordMethodBean(methodStartBean, messageData);
    }

    @Override
    public void recordMethodStart(MethodRecordMessageData messageData) {
        final MethodRecordBean parent = mMethodStack.isEmpty() ? null : mMethodStack.peek();
        final String beanId = mThreadName + "#" + (++mMethodCount);
        if (parent != null) parent.addChild(beanId);
        mMethodStack.push(
                new MethodRecordBean(beanId, parent == null ? null : parent.getId(), messageData,
                        mMethodStack.size()));
    }
}
