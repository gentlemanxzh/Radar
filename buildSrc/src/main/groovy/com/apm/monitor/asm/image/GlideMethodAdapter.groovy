package com.apm.monitor.asm.image

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

class GlideMethodAdapter  extends  AdviceAdapter{


    protected GlideMethodAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor)
    }

    /**
     * 方法退出时
     * 1.先拿到requestListeners
     * 2.然后对其进行修改
     * 3.将修改后的requestListeners设置回去
     * requestListeners=GlideHook.process(requestListeners);
     */
    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode)
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, "com/addcn/monitor/image/glide/GlideHook", "process", "(Ljava/lang/Object;)V", false);
    }
}