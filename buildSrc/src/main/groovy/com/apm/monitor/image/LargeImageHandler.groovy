package com.apm.monitor.image

import com.apm.monitor.asm.image.LargeImageMonitorClassVisitor
import com.apm.monitor.base.BaseHandler
import org.objectweb.asm.ClassWriter


class LargeImageHandler extends  BaseHandler {

    //处理文件
    @Override
    def createClassVisitor(int api,  ClassWriter classWriter) {
        return new LargeImageMonitorClassVisitor(api,classWriter)
    }
}