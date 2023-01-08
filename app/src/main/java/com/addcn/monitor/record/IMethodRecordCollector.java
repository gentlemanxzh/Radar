package com.addcn.monitor.record;

import java.util.List;

public interface IMethodRecordCollector {

    /**
     * 获取 方法收集信息
     * @return  List<List<MethodRecordMessageData>>
     */
    List<List<MethodRecordMessageData>> getRecordList();

    /**
     * 收集方法信息
     * @param data MethodRecordMessageData
     */
    void collectMethodInfo(MethodRecordMessageData data);


    /**
     * 重置信息收集器
     */
    void resetRecordList();


}
