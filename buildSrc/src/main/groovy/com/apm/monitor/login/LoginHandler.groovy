package com.apm.monitor.login

import com.apm.monitor.asm.click.DoubleClickClassVisitor
import com.apm.monitor.asm.login.LoginClassVisitor
import com.apm.monitor.base.BaseHandler
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class LoginHandler extends BaseHandler{

    @Override
    def createClassVisitor(int api, ClassWriter classWriter) {
        return null
    }

    @Override
    void handleDirectoryInput(File classFile) {
        if (classFile == null || !classFile.exists()) return
        if (classFile.isFile()) {
            if (!ignoreClass(classFile.name)) {
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
                ClassVisitor classVisitor = new LoginClassVisitor(classWriter)
                ClassReader classReader = new ClassReader(classFile.bytes)
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

    @Override
    void handlerJarInput(File file, File tmpFile) {
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
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
                ClassVisitor classVisitor = new LoginClassVisitor(classWriter)
                ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                byte[] code = classWriter.toByteArray()
                jarOutputStream.write(code)
            } else {
                jarOutputStream.putNextEntry(zipEntry)
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        jarFile.close()
    }
}
