package com.apm.monitor.click

import com.apm.monitor.base.BaseTransform


class ClickTransform extends BaseTransform {

    ClickHandler handler = new ClickHandler()

    @Override
    String getTransName() {
        return "ClickTransform"
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