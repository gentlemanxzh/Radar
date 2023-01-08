package com.apm.monitor

import com.apm.monitor.click.ClickMonitorExtension
import com.apm.monitor.image.LargeImageMonitorExtension
import com.apm.monitor.log.LogMonitorExtension
import com.apm.monitor.login.LoginMonitorExtension
import com.apm.monitor.method.MethodMonitorExtension
import com.apm.monitor.network.NetworkMonitorExtension
import com.apm.monitor.thread.ThreadMonitorExtension
import org.gradle.api.Action
import org.gradle.util.ConfigureUtil

class MonitorExtension {
    MethodMonitorExtension methodExt = new MethodMonitorExtension()
    LargeImageMonitorExtension imageExt = new LargeImageMonitorExtension()
    LogMonitorExtension logExt = new LogMonitorExtension()
    ClickMonitorExtension clickExt = new ClickMonitorExtension()
    LoginMonitorExtension loginExt = new LoginMonitorExtension()
    NetworkMonitorExtension networkExt = new NetworkMonitorExtension()
    ThreadMonitorExtension threadExt = new ThreadMonitorExtension()


    void method(Action<MethodMonitorExtension> action) {
        action.execute(method)
    }

    void method(Closure c) {
        ConfigureUtil.configure(c, methodExt)
    }

    void largeImage(Action<LargeImageMonitorExtension> action) {
        action.execute(largeImage)
    }

    void largeImage(Closure c) {
        ConfigureUtil.configure(c, imageExt)
    }

    void log(Action<LogMonitorExtension> action) {
        action.execute(log)
    }

    void log(Closure c) {
        ConfigureUtil.configure(c, logExt)
    }

    void click(Action<ClickMonitorExtension> action) {
        action.execute(click)
    }

    void click(Closure c) {
        ConfigureUtil.configure(c, clickExt)
    }

    void login(Action<LoginMonitorExtension> action) {
        action.execute(login)
    }

    void login(Closure c) {
        ConfigureUtil.configure(c, loginExt)
    }

    void network(Action<NetworkMonitorExtension> action) {
        action.execute(login)
    }

    void network(Closure c) {
        ConfigureUtil.configure(c, networkExt)
    }

    void thread(Action<ThreadMonitorExtension> action) {
        action.execute(login)
    }

    void thread(Closure c) {
        ConfigureUtil.configure(c, threadExt)
    }
}