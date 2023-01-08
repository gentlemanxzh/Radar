package com.apm.monitor

import com.android.build.gradle.AppExtension
import com.apm.monitor.asm.click.DoubleClickClassVisitor
import com.apm.monitor.asm.image.LargeImageMonitorClassVisitor
import com.apm.monitor.asm.log.MethodCallClassVisitor
import com.apm.monitor.asm.login.LoginClassVisitor
import com.apm.monitor.asm.method.MethodMonitorClassVisitor
import com.apm.monitor.asm.network.NetworkMonitorClassVisitor
import com.apm.monitor.asm.thread.ThreadClassVisitor
import com.apm.monitor.click.ClickMonitorExtension
import com.apm.monitor.click.ClickTransform
import com.apm.monitor.image.LargeImageMonitorExtension
import com.apm.monitor.image.LargeImageMonitorTransform
import com.apm.monitor.log.LogMonitorExtension
import com.apm.monitor.log.LogMonitorTransform
import com.apm.monitor.login.LoginMonitorExtension
import com.apm.monitor.login.LoginTransform
import com.apm.monitor.method.MethodMonitorExtension
import com.apm.monitor.method.MethodMonitorTransform
import com.apm.monitor.network.NetworkMonitorExtension
import com.apm.monitor.network.NetworkMonitorTransform
import com.apm.monitor.thread.ThreadMonitorExtension
import com.apm.monitor.thread.ThreadTransform
import org.apache.http.util.TextUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

class MonitorPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println "MonitorPlugin apply"

        //创建配置信息的扩展属性
        project.extensions.create("monitor", MonitorExtension)

        //Gradle配置阶段结束后
        project.afterEvaluate {
            MonitorExtension extension = project.getExtensions().findByType(MonitorExtension.class)
            initConfig(extension)
        }
        //注册Transform插件
        AppExtension appExtension = project.extensions.getByType(AppExtension)
        appExtension.registerTransform(new LargeImageMonitorTransform())
        appExtension.registerTransform(new MethodMonitorTransform())
        appExtension.registerTransform(new NetworkMonitorTransform())
        appExtension.registerTransform(new LogMonitorTransform())
        appExtension.registerTransform(new ClickTransform())
        appExtension.registerTransform(new ThreadTransform())
        appExtension.registerTransform(new LoginTransform())
    }

    //处理配置
    private static void initConfig(MonitorExtension extension) {
        println(extension.imageExt)
        println(extension.methodExt)
        initMethodExtension(extension.methodExt)
        initLargeImageExtension(extension.imageExt)
        initNetworkExtension(extension.networkExt)
        initLogExtension(extension.logExt)
        initLoginExtension(extension.loginExt)
        initThreadExtension(extension.threadExt)
        initClickExtension(extension.clickExt)

    }

    private static void initMethodExtension(MethodMonitorExtension extension) {
        MethodMonitorClassVisitor.enable = extension.enable
        MethodMonitorClassVisitor.isFullRecord = extension.fullRecord;
        String path = extension.filterFilePath
        if (!TextUtils.isEmpty(path)) {
            def filterFile = new File(path)
            filterFile.withReader { BufferedReader reader ->
                List<String> lines = reader.readLines();
                lines.each { String line -> MethodMonitorClassVisitor.filterSet.add(line.trim())
                }
            }
        }
    }

    private static void initLargeImageExtension(LargeImageMonitorExtension extension) {
        LargeImageMonitorClassVisitor.enable = extension.enable
    }

    private static void initNetworkExtension(NetworkMonitorExtension extension) {
        NetworkMonitorClassVisitor.enable = extension.enable
    }

    private static void initLogExtension(LogMonitorExtension extension) {
        MethodCallClassVisitor.enable = extension.enable
    }

    private static void initLoginExtension(LoginMonitorExtension extension) {
        LoginClassVisitor.enable = extension.enable
    }

    private static void initThreadExtension(ThreadMonitorExtension extension) {
        ThreadClassVisitor.enable = extension.enable
    }

    private static void initClickExtension(ClickMonitorExtension extension) {
        DoubleClickClassVisitor.enbale = extension.enable
    }

}