package com.apm.monitor.asm.network


import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class NetworkMonitorClassVisitor extends ClassVisitor {

    private String className;
    //是否启动网络检测
    public static boolean enable = true

    NetworkMonitorClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor)
    }


    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        this.className = name
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = this.cv.visitMethod(access, name, descriptor, signature, exceptions)
        if (!enable) {
            return methodVisitor
        }

        if (className == "okhttp3/OkHttpClient\$Builder" && name == "<init>" && descriptor == "()V") {
            return methodVisitor == null ? null : new OkHttpMethodAdapter(api, methodVisitor, access, name, descriptor)
        }
        return methodVisitor
    }
}