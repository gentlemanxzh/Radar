package com.apm.monitor.network


import com.apm.monitor.base.BaseTransform

/**
 * 把输入的 .class 文件转换为目标字节码文件
 */
class NetworkMonitorTransform extends BaseTransform {


    @Override
    String getTransName() {
        return "NetworkMonitor"
    }

    NetworkMonitorHandler handler = new NetworkMonitorHandler()

    @Override
    void handleDirectoryInput(File file) {
        handler.handleDirectoryInput(file)
    }

    @Override
    void handlerJarInput(File jarFile, File tmpFile) {
        handler.handlerJarInput(jarFile,tmpFile)
    }
}