package com.apm.monitor.asm.login

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*

class LoginClassVisitor extends ClassNode {


    private ClassVisitor classVisitor

    //是否启用登录拦截
    public static boolean enable = false

    LoginClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM9)
        this.classVisitor = classVisitor
    }

    @Override
    void visitEnd() {
        super.visitEnd()
        if (enable) {
            List<MethodNode> shouldHookMethodList = new ArrayList<>()
            methods.each { MethodNode methodNode ->
                if (hasAnnotation(methodNode)) {
                    shouldHookMethodList.add(methodNode)
                }
            }
            shouldHookMethodList.each {
                hookMethod(it)
            }
        }
        accept(classVisitor)
    }

    static void hookMethod(MethodNode methodNode) {
        def instructions = methodNode.instructions
        InsnList list = new InsnList()
        list.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "com/addcn/monitor/login/LoginHook",
                "isLogin",
                "()Z"
        ))
        LabelNode labelNode = new LabelNode()
        list.add(new JumpInsnNode(Opcodes.IFNE, labelNode))
        list.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "com/addcn/monitor/login/LoginHook",
                "starLoginPage",
                "()V"
        ))
        list.add(new InsnNode(Opcodes.RETURN))
        list.add(labelNode)
        instructions.insert(list)
    }


    static boolean hasAnnotation(MethodNode methodNode) {
        def annotations = methodNode.visibleAnnotations
        return annotations != null && annotations.find {
            it.desc == "Lcom/addcn/monitor/login/NeedLogin;"
        } != null

    }


}