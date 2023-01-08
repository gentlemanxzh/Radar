package com.apm.monitor.asm.thread

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.TypeInsnNode


class ThreadClassVisitor extends ClassNode {

    private static final String threadClass = "java/lang/Thread"
    private static final threadFactoryNewThreadMethodDesc =
            "newThread(Ljava/lang/Runnable;)Ljava/lang/Thread;"
    private static final String threadFactoryClass = "java/util/concurrent/ThreadFactory"
    private static final def executorsClass = "java/util/concurrent/Executors"
    public static boolean enable = false

    private static final def threadHookName = [
            "newFixedThreadPool",
            "newSingleThreadExecutor",
            "newCachedThreadPool",
            "newSingleThreadScheduledExecutor",
            "newScheduledThreadPool",
    ]

    private ClassVisitor classVisitor


    ThreadClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM9)
        this.classVisitor = classVisitor
    }

    @Override
    void visitEnd() {
        super.visitEnd()
        if (enable) {
            methods.each { MethodNode methodNode ->
                def instructions = methodNode.instructions
                if (instructions != null && instructions.size() > 0) {
                    instructions.each { instruction ->
                        if (instruction.opcode == Opcodes.NEW) {
                            def typeInsnNode = instruction as TypeInsnNode
                            if (typeInsnNode != null && typeInsnNode.desc == threadClass) {
                                //如果是在 ThreadFactory 内初始化线程，则不处理
                                if (!isThreadFactoryMethod(methodNode)) {
                                    transformNewThreadInstruction(methodNode, instruction)
                                }
                            }
                        } else if (instruction.opcode == Opcodes.INVOKESTATIC) {
                            def methodInsnNode = instruction as MethodInsnNode
                            if (methodInsnNode != null && methodInsnNode.owner == executorsClass) {
                                transformInvokeExecutorsInstruction(methodNode, methodInsnNode)
                            }
                        }
                    }
                }
            }
        }
        if (classVisitor != null) {
            accept(classVisitor)
        }
    }

    private void transformInvokeExecutorsInstruction(MethodNode methodNode,
                                                     MethodInsnNode methodInsnNode) {
        String method = threadHookName.find { it == methodInsnNode.name }
        if (method != null) {
            //将 Executors 替换为 CustomExecutors
            methodInsnNode.owner = "com/addcn/monitor/thread/CustomExecutors"
            //为调用 newFixedThreadPool 等方法的指令多插入一个 String 类型的方法入参参数声明
            insertArgument(methodInsnNode, String.class)
            //将 className 作为上述 String 参数的入参参数
            methodNode.instructions.insertBefore(methodInsnNode, new LdcInsnNode(substringAfterLast(this.name, '/')))
        }

    }

    private void transformNewThreadInstruction(MethodNode methodNode, TypeInsnNode typeInsnNode) {
        def instructions = methodNode.instructions
        def typeInsnNodeIndex = instructions.indexOf(typeInsnNode)
        for (int i = typeInsnNodeIndex + 1; i < instructions.size(); i++) {
            def node = instructions[i]
            if (node instanceof MethodInsnNode && node && isThreadInitMethod(node)) {
                //将 Thread 替换为 自定义的 Thread
                typeInsnNode.desc = "com/addcn/monitor/thread/CustomThread"
                node.owner = "com/addcn/monitor/thread/CustomThread"
                //为调用 Thread 构造函数的指令多插入一个 String 类型的方法入参参数声明
                insertArgument(node, String.class)
                //将 ClassName 作为构造参数传给自定义Thread
                instructions.insertBefore(node, new LdcInsnNode(substringAfterLast(this.name, '/')))
                break
            }
        }
    }

    private String substringAfterLast(String text, String delimiter) {
        def index = findLastIndexOf {
            it == delimiter
        }
        return text.substring(index + 1, text.length())
    }

    private void insertArgument(MethodInsnNode methodInsnNode, Class argumentType) {
        def type = Type.getMethodType(methodInsnNode.desc)
        def argumentTypes = type.argumentTypes
        def returnType = type.returnType
        def newArgumentTypes = new Type[argumentTypes.size() + 1]
        System.arraycopy(argumentTypes, 0, newArgumentTypes, 0, argumentTypes.length)
        newArgumentTypes[newArgumentTypes.size() - 1] = Type.getType(argumentType)
        methodInsnNode.desc = Type.getMethodDescriptor(returnType, newArgumentTypes)
    }


    private boolean isThreadInitMethod(MethodInsnNode node) {
        return node.owner == threadClass && node.name == "<init>"
    }

    private boolean isThreadFactoryMethod(MethodNode methodNode) {
        if (this.interfaces != null && this.interfaces.contains(threadFactoryClass) && ((methodNode.name + methodNode.desc) == threadFactoryNewThreadMethodDesc)) {
            return true
        }
        return false
    }
}