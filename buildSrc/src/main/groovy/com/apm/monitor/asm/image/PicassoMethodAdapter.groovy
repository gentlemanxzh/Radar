package com.apm.monitor.asm.image

import groovyjarjarasm.asm.MethodVisitor
import groovyjarjarasm.asm.commons.AdviceAdapter

public class PicassoMethodAdapter extends AdviceAdapter {

    protected PicassoMethodAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor)
    }

    /**
     * 方法进入时
     * 1.拿到方法第一个参数Uri
     * 2.拿到方法第四个参数 List<Transformation> transformations
     * 3.把它们传入hook方法
     * 4.在方法中加入我们自己的Transformation
     * 5.将设置好以后的 List<Transformation> transformations返回给第四个参数
     *  transformations = PicassoHook.process(uri,transformations,resourceId,targetWidth,targetHeight);
     */
    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 4);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitVarInsn(ILOAD, 5);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitMethodInsn(INVOKESTATIC, "org/zzy/lib/largeimage/aop/picasso/PicassoHook", "process", "(Landroid/net/Uri;Ljava/util/List;III)Ljava/util/List;", false);
        mv.visitVarInsn(ASTORE, 4);
    }
}