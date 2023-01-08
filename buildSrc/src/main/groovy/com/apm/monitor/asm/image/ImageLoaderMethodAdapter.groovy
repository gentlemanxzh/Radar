package com.apm.monitor.asm.image

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

class ImageLoaderMethodAdapter extends AdviceAdapter{


    protected ImageLoaderMethodAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor)
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        mv.visitVarInsn(ALOAD, 5);
        mv.visitMethodInsn(INVOKESTATIC, "org/zzy/lib/largeimage/aop/imageloader/ImageLoaderHook", "process", "(Lcom/nostra13/universalimageloader/core/listener/ImageLoadingListener;)Lcom/nostra13/universalimageloader/core/listener/ImageLoadingListener;", false);
        mv.visitVarInsn(ASTORE, 5);
    }
}