package com.apm.monitor.image


/**
 * 用来接收Gradle配置里面的闭包
 */
class LargeImageMonitorExtension {
    boolean enable = true


    @Override
    String toString() {
        return "LargeImageMonitorExtension{" +
                "enable=" + enable +
                '}';
    }
}