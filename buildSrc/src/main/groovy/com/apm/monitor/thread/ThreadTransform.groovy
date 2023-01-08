package com.apm.monitor.thread

import com.apm.monitor.base.BaseTransform

class ThreadTransform extends BaseTransform {

    ThreadHandler handler = new ThreadHandler()

    @Override
    String getTransName() {
        return "ThreadTransform"
    }

    @Override
    void handleDirectoryInput(File file) {
        handler.handleDirectoryInput(file)
    }

    @Override
    void handlerJarInput(File jarFile, File tmpFile) {
        handler.handlerJarInput(jarFile, tmpFile)
    }
}