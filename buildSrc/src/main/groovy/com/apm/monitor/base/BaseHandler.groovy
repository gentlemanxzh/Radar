package com.apm.monitor.base


import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

abstract class BaseHandler {

    abstract createClassVisitor(int api,ClassWriter classWriter)

    //处理文件
    void handleDirectoryInput(File classFile) {
        if (classFile == null || !classFile.exists()) return
        if (classFile.isFile()) {
            if (!ignoreClass(classFile.name)) {
                ClassReader classReader = new ClassReader(classFile.bytes)
                ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                ClassVisitor classVisitor = createClassVisitor(Opcodes.ASM9, classWriter)
                classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                FileOutputStream fos = new FileOutputStream(classFile.absolutePath)
                fos.write(classWriter.toByteArray())
                fos.close()
            }
        } else {
            //文件夹
            classFile.listFiles().each {
                handleDirectoryInput(it)
            }
        }

    }

    //处理Jar包
    void handlerJarInput(File file,File tmpFile) {
            JarFile jarFile = new JarFile(file)
            Enumeration enumeration = jarFile.entries()
            // 避免上次的缓存被重复插入
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                ZipEntry zipEntry = new ZipEntry(entryName)
                InputStream inputStream = jarFile.getInputStream(jarEntry)
                if (!ignoreClass(entryName)) {
                    jarOutputStream.putNextEntry(zipEntry)
                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor classVisitor = createClassVisitor(Opcodes.ASM9, classWriter)
                    classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()
                    jarOutputStream.write(code)
                }else {
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }
            jarOutputStream.close()
            jarFile.close()
    }


    /**
     * 需要忽略的类
     * @param className 类名
     * @return true 则忽略，false 则不忽略
     */
    static boolean ignoreClass(String className) {
        return (!className.endsWith(".class")
                || className.startsWith("R\$")
                || className.startsWith("R2\$")
                || "R.class" == className
                || "R2.class" == className
                || "BuildConfig.class" == className)
    }
}