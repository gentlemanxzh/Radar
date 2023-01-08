package com.apm.monitor.asm.click

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*

class DoubleClickClassVisitor extends ClassNode {

    String interfaceName = "android/view/View\$OnClickListener"
    String methodName = "onClick"
    String nameWithDesc = "onClick(Landroid/view/View;)V"

    private ClassVisitor classVisitor

    public static boolean enbale = false

    DoubleClickClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM9)
        this.classVisitor = classVisitor
    }

    @Override
    void visitEnd() {
        super.visitEnd()
        if (enbale) {
            List<MethodNode> shouldHookMethodList = new ArrayList<>()

            methods.each { MethodNode methodNode ->

                //使用了匿名内部类的情况
                if (hasUncheckViewOnClickAnnotation(methodNode)) {
                    //不处理包含 UncheckViewOnClick 注解的方法

                } else if (isHookPoint(methodNode)) {
                    shouldHookMethodList.add(methodNode)
                }

                //判断方法内部是否有需要处理的 lambda 表达式
                List<InvokeDynamicInsnNode> dynamicInsnNodes = filterLambda(methodNode)
                dynamicInsnNodes.each {
                    Handle handle = it.bsmArgs[1] as Handle
                    if (handle != null) {
                        String nameWithDesc = handle.name + handle.desc
                        MethodNode node = methods.find { it.name + it.desc == nameWithDesc }
                        shouldHookMethodList.add(node)
                    }
                }
            }
            shouldHookMethodList.each {
                hookMethod(it)
            }
        }
        accept(classVisitor)

    }

    void hookMethod(MethodNode methodNode) {
        Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc)
        int viewArgumentIndex = -1
        if (argumentTypes != null) {
            viewArgumentIndex = argumentTypes.findIndexOf { it.descriptor == "Landroid/view/View;" }
        }
        if (viewArgumentIndex >= 0) {
            def instructions = methodNode.instructions
            if (instructions != null && instructions.size() > 0) {
                InsnList list = new InsnList()
                int position = getVisitPosition(argumentTypes, viewArgumentIndex, isStatic(methodNode))
                list.add(new VarInsnNode(Opcodes.ALOAD, position))
                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "com/addcn/monitor/click/ViewDoubleClickCheck",
                        "onClick",
                        "(Landroid/view/View;)Z"))
                LabelNode labelNode = new LabelNode()
                list.add(new JumpInsnNode(Opcodes.IFNE, labelNode))
                list.add(new InsnNode(Opcodes.RETURN))
                list.add(labelNode)
                instructions.insert(list)
            }
        }
    }


    int getVisitPosition(Type[] argumentTypes,
                         int parameterIndex,
                         boolean isStaticMethod) {

        if (parameterIndex < 0 || parameterIndex >= (argumentTypes.length)) {
            throw Error("getVisitPosition error")
        }

        if (parameterIndex == 0) {
            if (isStaticMethod) {
                return 0
            } else {
                return 1
            }
        } else {
            int position = getVisitPosition(argumentTypes, parameterIndex - 1, isStaticMethod)
            position = position + ((argumentTypes[parameterIndex - 1].size))
            return position
        }
    }

    List<InvokeDynamicInsnNode> filterLambda(MethodNode methodNode) {
        InsnList mInstructions = methodNode.instructions != null ? methodNode.instructions : new InsnList()
        List<InvokeDynamicInsnNode> dynamicList = new ArrayList<>()
        mInstructions.each { instruction ->
            if (instruction instanceof InvokeDynamicInsnNode) {
                instruction as InvokeDynamicInsnNode
                String nodeName = instruction.name
                String nodeDesc = instruction.desc
                if (nodeName == methodName && nodeDesc.endsWith("Landroid/view/View\$OnClickListener;")) {
                    dynamicList.add(instruction)
                }
            }
        }
        return dynamicList
    }


    /**
     * 是否包含 UncheckViewOnClick 注解
     * @return
     */
    static boolean hasUncheckViewOnClickAnnotation(MethodNode methodNode) {
        methodNode.visibleAnnotations.find {
            it.desc == "Lcom/addcn/monitor/click/UncheckViewOnClick;"
        }
    }

    /**
     * 是否需要Hook
     * @param methodNode
     * @return
     */
    boolean isHookPoint(MethodNode methodNode) {
        def strings = interfaces
        if (strings.isEmpty()) {
            return false
        }
        return strings.contains(interfaceName) && ((methodNode.name + methodNode.desc) == nameWithDesc)

    }

    /**
     * 是否是静态方法
     * @param methodNode
     * @return true 为静态方法
     */
    static boolean isStatic(MethodNode methodNode) {
        return (methodNode.access & Opcodes.ACC_STATIC) != 0
    }


}