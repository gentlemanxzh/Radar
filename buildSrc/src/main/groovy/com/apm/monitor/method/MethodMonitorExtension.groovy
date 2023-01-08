package com.apm.monitor.method


/**
 * 用来接收Gradle配置里面的闭包
 */
class MethodMonitorExtension {
    //是否启动方法耗时统计
    boolean enable = false
    //是否全量收集
    boolean fullRecord = true
    //过滤的文件路径
    String filterFilePath = ""

}