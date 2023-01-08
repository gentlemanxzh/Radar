package com.apm.monitor.asm.method

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class MethodMonitorClassVisitor extends ClassVisitor {
    public static String NO_METHOD_RECORD_DESC = "Lcom/addcn/monitor/annotation/NoMethodRecord;"
    public static String METHOD_RECORD_DESC = "Lcom/addcn/monitor/annotation/MethodRecord;"
    //过滤的方法
    public static Set<String> filterSet = new HashSet()
    //当前Class 是否需要进行处理
    private boolean isHandler = true
    //当前的Class 名称
    private String className
    //是否启动方法耗时统计
    public static boolean enable = false
    //是否全量统计
    public static boolean isFullRecord = true


    MethodMonitorClassVisitor(int api, ClassVisitor classVisitor) {
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
        if (isHandler) {
            isHandler = NO_METHOD_RECORD_DESC != descriptor
        }
        return super.visitAnnotation(descriptor, visible)
    }


    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = this.cv.visitMethod(access, name, descriptor, signature, exceptions)
        //如果插件不启动，则不处理
        if (!enable) {
            return methodVisitor
        }
        //如果方法在过滤列表则不处理
        String methodKey = className + name + descriptor
        if (filterSet.contains(methodKey)) {
            return methodVisitor
        }

        if (isHandler) {
            return new MethodMonitorAdviceAdapter(api, methodVisitor, access, name, descriptor, className)
        }else {
            return methodVisitor
        }

    }
}