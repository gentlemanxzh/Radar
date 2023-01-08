package com.apm.monitor.method


import com.android.build.gradle.AppExtension
import com.apm.monitor.asm.method.MethodMonitorClassVisitor
import org.apache.http.util.TextUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

class MethodMonitorPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println "MethodMonitorPlugin apply"

        //创建配置信息的扩展属性
        project.extensions.create("methodMonitor", MethodMonitorExtension)
        //Gradle配置阶段结束后
        project.afterEvaluate {
            MethodMonitorExtension extension = project.getExtensions().findByType(MethodMonitorExtension.class)
            initConfig(extension)
        }
        //注册Transform插件
        AppExtension appExtension = project.extensions.getByType(AppExtension)
        MethodMonitorTransform transform = new MethodMonitorTransform()
        appExtension.registerTransform(transform)
    }

    //处理配置
    private static void initConfig(MethodMonitorExtension extension) {
        String path = extension.filterFilePath
        if (!TextUtils.isEmpty(path)) {
            def filterFile = new File(path)
            filterFile.withReader {BufferedReader reader->
                List<String> lines = reader.readLines();
                lines.each {String line->
                  MethodMonitorClassVisitor.filterSet.add(line.trim())
                }
            }
        }
    }
}