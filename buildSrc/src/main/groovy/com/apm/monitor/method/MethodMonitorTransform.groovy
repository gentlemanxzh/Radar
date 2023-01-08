package com.apm.monitor.method


import com.apm.monitor.base.BaseTransform

/**
 * 把输入的 .class 文件转换为目标字节码文件
 */
class MethodMonitorTransform extends BaseTransform {


    @Override
    String getTransName() {
        return "MethodMonitor"
    }

    MethodTimerHandler handler = new MethodTimerHandler()

    @Override
    void handleDirectoryInput(File file) {
        handler.handleDirectoryInput(file)
    }

    @Override
    void handlerJarInput(File jarFile, File tmpFile) {
        handler.handlerJarInput(jarFile,tmpFile)
    }
}