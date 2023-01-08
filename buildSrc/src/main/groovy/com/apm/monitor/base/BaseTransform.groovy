package com.apm.monitor.base

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils

abstract class BaseTransform extends Transform {

    abstract String getTransName()

    /**
     * 每一个 Transform 都有一个与之对应的 Transform task，
     * 这里便是返回的 task name。它会出现在
     * app/build/intermediates/transforms 目录下
     *
     */
    @Override
    String getName() {
        return getTransName()
    }

    /**
     * 需要处理的数据类型，目前 ContentType
     * 有六种枚举类型，通常我们使用比较频繁的有前两种：
     * 1、CONTENT_CLASS：表示需要处理 java 的 class 文件。
     * 2、CONTENT_JARS：表示需要处理 java 的 class 与 资源文件。
     * 3、CONTENT_RESOURCES：表示需要处理 java 的资源文件。
     * 4、CONTENT_NATIVE_LIBS：表示需要处理 native 库的代码。
     * 5、CONTENT_DEX：表示需要处理 DEX 文件。
     * 6、CONTENT_DEX_WITH_RESOURCES：表示需要处理 DEX 与 java 的资源文件。
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        // 用于确定我们需要对哪些类型的结果进行转换：如字节码、资源⽂件等等。
        return TransformManager.CONTENT_CLASS
    }

    /**
     *  表示 Transform 要操作的内容范围，目前 Scope 有五种基本类型：
     *    1、PROJECT                   只有项目内容
     *    2、SUB_PROJECTS              只有子项目
     *    3、EXTERNAL_LIBRARIES        只有外部库
     *    4、TESTED_CODE               由当前变体（包括依赖项）所测试的代码
     *    5、PROVIDED_ONLY             只提供本地或远程依赖项
     *    SCOPE_FULL_PROJECT 是一个 Scope 集合，包含 Scope.PROJECT,
     *    Scope.SUB_PROJECTS, Scope.EXTERNAL_LIBRARIES 这三项，即当前 Transform
     *    的作用域包括当前项目、子项目以及外部的依赖库
     *
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * 表示是否支持增量更新。
     * 如果返回 true，TransformInput 会包含一份修改的文件列表
     * 如果返回 false，会进行全量编译，删除上一次的输出内容
     */
    @Override
    boolean isIncremental() {
        return false
    }

    /**
     * 所有的class收集好，会被打包传入此方法
     * 进行具体的 转换过程
     * @param transformInvocation
     * @throws com.android.build.api.transform.TransformException* @throws InterruptedException* @throws IOException
     */
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        //1. 遍历所有的Input（必需）
        //2. 对Input进行二次处理
        //3. 将Input拷贝到目标目录（必需）


        transformInvocation.inputs.each {
            //遍历文件夹的Input,并拷贝到目标目录
            it.directoryInputs.each { directoryInput ->
                def destDir = transformInvocation.outputProvider
                        .getContentLocation(directoryInput.name,
                                directoryInput.contentTypes,
                                directoryInput.scopes,
                                Format.DIRECTORY)
                handleDirectoryInput(directoryInput.file)
                FileUtils.copyDirectory(directoryInput.file, destDir)
            }
            //遍历Jar的Input，并拷贝到目标Jar
            it.jarInputs.each { jarInput ->
                if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
                    // 截取文件路径的 md5 值重命名输出文件，避免同名导致覆盖的情况出现
                    String jarName = jarInput.name
                    String md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                    if (jarName.endsWith(".jar")) {
                        jarName = jarName.substring(0, jarName.length() - 4)
                    }
                    File tmpFile = new File(jarInput.file.getParent() + File.separator + "classes_temp.jar")
                    handlerJarInput(jarInput.file, tmpFile)
                    // 生成输出路径 dest：./app/build/intermediates/transforms/xxxTransform/...
                    def destJar = transformInvocation.outputProvider
                            .getContentLocation(jarName + md5Name,
                                    jarInput.contentTypes,
                                    jarInput.scopes,
                                    Format.JAR)
                    FileUtils.copyFile(tmpFile, destJar)
                    tmpFile.delete()
                }
            }
        }
    }


    abstract void handleDirectoryInput(File file)

    abstract void handlerJarInput(File jarFile, File tmpFile)

}