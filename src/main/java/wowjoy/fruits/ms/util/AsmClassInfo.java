package wowjoy.fruits.ms.util;

import jdk.internal.org.objectweb.asm.*;
import org.assertj.core.util.Lists;
import wowjoy.fruits.ms.exception.CheckException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Created by wangziwen on 2017/11/22.
 */
public class AsmClassInfo {
    private final Class aClass;
    private final ClassReader classReader;

    public AsmClassInfo(Class aClass) {
        this.aClass = aClass;
        try {
            this.classReader = new ClassReader(this.getaClass().getName());
        } catch (IOException e) {
            e.printStackTrace();
            throw new CheckException("指定类不存在");
        }
    }

    private Class getaClass() {
        if (aClass == null)
            throw new CheckException("class不存在");
        return aClass;
    }

    public static AsmClassInfo newInstance(Class aClass) {
        return new AsmClassInfo(aClass);
    }

    public static AsmClassInfo newInstance(String aClass) {
        return newInstance(toClass(aClass));
    }

    public List<String> findParameterName(Method method) {
        List<String> paramNames = Lists.newArrayList();
        classReader.accept(new ClassVisitor(Opcodes.ASM5, new ClassWriter(ClassWriter.COMPUTE_MAXS)) {
            @Override
            public MethodVisitor visitMethod(int i, String methodName, String s1, String s2, String[] strings) {
                if (method.getName().equals(methodName))
                    return new MethodVisitor(Opcodes.ASM5, super.visitMethod(i, methodName, s1, s2, strings)) {
                        @Override
                        public void visitLocalVariable(String name, String s1, String s2, Label label, Label label1, int i) {
                            int index = --i;
                            if (Modifier.isStatic(method.getModifiers()))
                                index = i;
                            if (index >= 0 && index < method.getParameters().length)
                                paramNames.add(index, name);
                        }
                    };
                return super.visitMethod(i, methodName, s1, s2, strings);
            }
        }, 0);
        return paramNames;
    }

    private static Class<?> toClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new CheckException("class不存在");
        }
    }

    public List<String> findParameterName(String methodName) {
        Method method = findMethod(methodName);
        if (method != null)
            return findParameterName(method);
        return Lists.emptyList();
    }

    public Method findMethod(String methodName) {
        for (Method method : aClass.getMethods()) {
            if (method.getName().equals(methodName))
                return method;
        }
        return null;
    }
}
