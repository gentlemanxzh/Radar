package com.apm.monitor.log

import com.apm.monitor.asm.log.MethodCallClassVisitor
import com.apm.monitor.base.BaseHandler
import org.objectweb.asm.ClassWriter

class LogMonitorHandler extends BaseHandler {

    //处理文件
    @Override
    def createClassVisitor(int api, ClassWriter classWriter) {
        return new MethodCallClassVisitor(classWriter)
    }
}