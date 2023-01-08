package com.apm.monitor.asm.method

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

class MethodMonitorAdviceAdapter extends AdviceAdapter {


    private String className
    private String methodName
    private String descriptor
    private boolean isHandleMethod = true


    MethodMonitorAdviceAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor, String className) {
        super(api, methodVisitor, access, name, descriptor)
        this.className = className
        this.methodName = name
        this.descriptor = descriptor
        if (MethodMonitorClassVisitor.isFullRecord) {
            isHandleMethod = !(name == "<init>" || name == "<clinit>")
        } else {
            isHandleMethod = false
        }

    }

    @Override
    AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
//        println("className = " + className + " methodName = " + methodName + " descriptor = " + descriptor)
        if (!MethodMonitorClassVisitor.isFullRecord) {
            isHandleMethod = MethodMonitorClassVisitor.METHOD_RECORD_DESC==descriptor
        }
        return super.visitAnnotation(descriptor, visible)
    }

    @Override
    protected void onMethodEnter() {
        if (!isHandleMethod) {
            super.onMethodEnter()
            return
        }
        mv.visitLdcInsn(className)
        mv.visitLdcInsn(name)
        mv.visitLdcInsn(descriptor)
        mv.visitMethodInsn(INVOKESTATIC
                , "com/addcn/monitor/record/MethodRecordManager"
                , "recordMethodStart"
                , "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"
                , false)
        super.onMethodEnter()
    }

    @Override
    protected void onMethodExit(int opcode) {
        if (!isHandleMethod) {
            super.onMethodEnter()
            return
        }
        mv.visitLdcInsn(className)
        mv.visitLdcInsn(name)
        mv.visitLdcInsn(descriptor)
        mv.visitMethodInsn(INVOKESTATIC
                , "com/addcn/monitor/record/MethodRecordManager"
                , "recordMethodEnd"
                , "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"
                , false
        )
        super.onMethodExit(opcode)
    }
}