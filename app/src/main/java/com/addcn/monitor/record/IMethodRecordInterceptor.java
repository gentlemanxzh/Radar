package com.addcn.monitor.record;

public interface IMethodRecordInterceptor {

    boolean onMethodIntercept(String className,String methodName,String desc);
}
