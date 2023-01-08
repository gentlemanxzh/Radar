package com.apm.monitor.asm.network

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

class OkHttpMethodAdapter extends AdviceAdapter {


    protected OkHttpMethodAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor)
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode)
        //添加应用拦截器
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, "okhttp3/OkHttpClient\$Builder", "interceptors", "Ljava/util/List;");
        mv.visitMethodInsn(INVOKESTATIC, "com/addcn/monitor/image/LargeImageManager", "getInstance", "()Lcom/addcn/monitor/image/LargeImageManager;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/addcn/monitor/image/LargeImageManager", "getOkHttpInterceptors", "()Ljava/util/List;", false);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true);
        mv.visitInsn(POP);
        //添加网络拦截器
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, "okhttp3/OkHttpClient\$Builder", "networkInterceptors", "Ljava/util/List;");
        mv.visitMethodInsn(INVOKESTATIC, "com/addcn/monitor/image/LargeImageManager", "getInstance", "()Lcom/addcn/monitor/image/LargeImageManager;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/addcn/monitor/image/LargeImageManager", "getOkHttpNetworkInterceptors", "()Ljava/util/List;", false);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true);
        mv.visitInsn(POP);
    }
}