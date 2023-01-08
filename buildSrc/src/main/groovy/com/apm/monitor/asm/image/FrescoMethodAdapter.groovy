package com.apm.monitor.asm.image

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

class FrescoMethodAdapter extends  AdviceAdapter{


    protected FrescoMethodAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor)
    }


    /**
     * 方法进入时
     * 1.调用ImageRequestBuilder的getSourceUri()
     * 2.调用getPostprocessor()
     * 3.设置进FrescoHook的process方法
     * 4.将返回的Postprocessor再设置进ImageRequestBuilder
     * builder.setPostprocessor(FrescoHook.process(builder.getSourceUri()，builder.getPostprocessor(),getResizeOptions()));
     */
    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/imagepipeline/request/ImageRequestBuilder", "getSourceUri", "()Landroid/net/Uri;", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/imagepipeline/request/ImageRequestBuilder", "getPostprocessor", "()Lcom/facebook/imagepipeline/request/Postprocessor;", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/imagepipeline/request/ImageRequestBuilder", "getResizeOptions", "()Lcom/facebook/imagepipeline/common/ResizeOptions;", false);
        mv.visitMethodInsn(INVOKESTATIC, "org/zzy/lib/largeimage/aop/fresco/FrescoHook", "process", "(Landroid/net/Uri;Lcom/facebook/imagepipeline/request/Postprocessor;Lcom/facebook/imagepipeline/common/ResizeOptions;)Lcom/facebook/imagepipeline/request/Postprocessor;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/imagepipeline/request/ImageRequestBuilder", "setPostprocessor", "(Lcom/facebook/imagepipeline/request/Postprocessor;)Lcom/facebook/imagepipeline/request/ImageRequestBuilder;", false);
    }
}