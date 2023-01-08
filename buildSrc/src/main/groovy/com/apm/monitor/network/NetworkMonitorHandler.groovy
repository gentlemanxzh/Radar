package com.apm.monitor.network

import com.apm.monitor.asm.network.NetworkMonitorClassVisitor
import com.apm.monitor.base.BaseHandler
import org.objectweb.asm.ClassWriter

class NetworkMonitorHandler extends BaseHandler{

    //处理文件
    @Override
    def createClassVisitor(int api, ClassWriter classWriter) {
        return new NetworkMonitorClassVisitor(api,classWriter)
    }
}