package com.addcn.monitor.record;

import java.util.Map;

public interface IMethodRecorder {
     boolean debugStackEmpty();

     Map<String,MethodRecordBean> getMethodRecordMap();

     String getRecorderName();


     void recordMethodEnd(MethodRecordMessageData messageData);

     void recordMethodStart(MethodRecordMessageData  messageData);

}
