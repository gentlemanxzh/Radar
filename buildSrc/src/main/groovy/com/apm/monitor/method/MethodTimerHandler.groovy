package com.apm.monitor.method

import com.apm.monitor.asm.method.MethodMonitorClassVisitor
import com.apm.monitor.base.BaseHandler
import org.objectweb.asm.ClassWriter

class MethodTimerHandler extends BaseHandler{

    //处理文件

    @Override
    def createClassVisitor(int api, ClassWriter classWriter) {
        return new MethodMonitorClassVisitor(api,classWriter)
    }
}