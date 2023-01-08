package com.apm.monitor.asm.image

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class LargeImageMonitorClassVisitor extends ClassVisitor {

    //当前的Class 名称
    private String className
    //是否启动大图检测
    public static boolean enable = false


    LargeImageMonitorClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        this.className = name
    }

    /**
     * 获取或者修改注解
     * @param descriptor
     * @param visible
     * @return
     */
    @Override
    AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return super.visitAnnotation(descriptor, visible)
    }


    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = this.cv.visitMethod(access, name, descriptor, signature, exceptions)
        if (!enable) {
            return methodVisitor
        }

        //对Glide4.11版本的SingleRequest类的构造方法进行字节码修改
        if (className == "com/bumptech/glide/request/SingleRequest"
                && name == "<init>"
                && descriptor != null) {
            return methodVisitor == null ? null : new GlideMethodAdapter(api, methodVisitor, access, name, descriptor)
        }

        return methodVisitor

    }
}