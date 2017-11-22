import org.junit.Test;
import org.objectweb.asm.*;
import wowjoy.fruits.ms.controller.ListController;
import wowjoy.fruits.ms.module.list.FruitListVo;

import java.io.IOException;

/**
 * Created by wangziwen on 2017/9/14.
 */
public class ExtendsTest {
    class Test1 {
        public Test1() {
            this.name = "23232";
        }

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    class Test2 extends Test1 {
        public Test2() {
            this.setName(null);
        }
    }

    @Test
    public void test() throws IOException {
        try {
            System.out.println(ListController.class.getMethod("insertProject", FruitListVo.class).getParameters());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        ClassReader classReader = new ClassReader("wowjoy.fruits.ms.controller.ListController");
        MyVisitor myVisitor = new MyVisitor();
        classReader.accept(myVisitor, 0);
    }

    private class MyVisitor extends ClassVisitor {

        public MyVisitor() {
            super(Opcodes.ASM5, new ClassWriter(ClassWriter.COMPUTE_MAXS));
        }

        @Override
        public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
            MethodVisitor methodVisitor = this.cv.visitMethod(i, s, s1, s2, strings);
            System.out.print(s + "-");
            return new MethodVisitor(Opcodes.ASM5, methodVisitor) {
                @Override
                public void visitLocalVariable(String s, String s1, String s2, Label label, Label label1, int i) {
//                    System.out.println(s + "," + s1 + "," + s2 + "," + label + "," + label1 + "," + i);
                }

                @Override
                public void visitParameter(String s, int i) {
                    System.out.println(s + "," + i);
                }
            };
        }
    }

}
