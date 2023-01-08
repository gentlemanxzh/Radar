package com.addcn.monitor.record;

import android.util.Log;

import com.addcn.monitor.annotation.NoMethodRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoMethodRecord
public class MethodRecordAggregator {

    private Map<String, IMethodRecorder> recorderMap = null;


    private void recordMethodEnd(MethodRecordMessageData data) {
        IMethodRecorder iMethodRecorder = this.recorderMap.get(data.getThreadName());
        if (iMethodRecorder != null) {
            iMethodRecorder.recordMethodEnd(data);
        }
    }

    private void recordMethodStart(MethodRecordMessageData data) {
        String threadName = data.getThreadName();
        IMethodRecorder iMethodRecorder = this.recorderMap.get(threadName);
        if (iMethodRecorder != null) {
            iMethodRecorder.recordMethodStart(data);
            return;
        }
        IMethodRecorder recorder = new ThreadMethodRecorder(threadName);
        this.recorderMap.put(threadName, recorder);
        recorder.recordMethodStart(data);
    }


    public Map<String, IMethodRecorder> getRecorderMap(IMethodRecordCollector iMethodRecordCollector) {
        Map<String, IMethodRecorder> map = this.recorderMap;
        if (!(map == null || map.isEmpty())) {
            return this.recorderMap;
        }
        this.recorderMap = new HashMap<>();
        for (List<MethodRecordMessageData> list : iMethodRecordCollector.getRecordList()) {
            recordHandler(list);
        }
        iMethodRecordCollector.resetRecordList();
        printRecorder();
        return this.recorderMap;
    }


    private void printRecorder() {
        Collection<IMethodRecorder> values = this.recorderMap.values();
        List<IMethodRecorder> arrayList = new ArrayList<>();
        for (IMethodRecorder iMethodRecorder : values) {
            if (!iMethodRecorder.debugStackEmpty()) {
                arrayList.add(iMethodRecorder);
            }
        }
        for (IMethodRecorder iMethodRecorder : arrayList) {
            Log.d("debugThreadStack", iMethodRecorder.getRecorderName() + " stack is not empty");
        }
    }


    private void recordHandler(List<MethodRecordMessageData> list) {
        for (MethodRecordMessageData data : list) {
            if (data == null) continue;
            if (data.isStart()){
                recordMethodStart(data);
            }else {
                recordMethodEnd(data);
            }
        }
    }

    public void reset() {
        this.recorderMap.clear();
    }
}
