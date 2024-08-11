## 介绍
一个适用于 Android 的字节码插桩库，支持以下功能：

- 方法耗时检测
- View Click 双击防抖
- 大图检测功能
- 线程治理
- 日志清除
- 登录拦截
## 接入指南
在 `app`工程中的`build.gradle`文件引入插件
```c
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'kotlin-kapt'
    id 'com.apm.monitor'//引入插件
}
```
在项目主模块中应用插件，需要哪些功能点就为其设置对应的参数
```c
monitor {
    method {
        enable = true
        fullRecord = true
    }

    largeImage {
        enable = true
    }

    log {
        enable = true
    }

    click {
        enable = true
    }

    network {
        enable = true
    }

    login {
        enable = true
    }

    thread {
        enable = true
    }
}
```
### 耗时方法检测
耗时方法检测的可选配置如下：
```c
    method {
        enable = true//是否启动方法耗时统计
        fullRecord = true//是否全量收集
        filterFilePath = ''//过滤的文件路径
    }
```
### View Click 双击防抖
双击防抖的可选配置如下：
```c

    click {
        enable = true//是否启动双击防抖
    }
```
### 大图检测
大图检测的可选配置如下：
```c
    largeImage {
        enable = true //是否启动大图检测
    }
    network {
        enable = true //是否启动网络大图检测
    }

```
### 登录拦截
登录检测的可选配置如下：
```c
    login {
        enable = true
    }
```
### 线程治理
线程治理的可选配置如下：
```c
    thread {
        enable = true
    }
```
