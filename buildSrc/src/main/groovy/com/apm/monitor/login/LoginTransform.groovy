package com.apm.monitor.login

import com.apm.monitor.base.BaseTransform

class LoginTransform extends BaseTransform {

    LoginHandler handler = new LoginHandler()

    @Override
    String getTransName() {
        return "LoginTransform"
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