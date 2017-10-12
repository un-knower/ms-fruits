import org.junit.Test;

/**
 * Created by wangziwen on 2017/9/14.
 */
public class ExtendsTest {
    class Test1{
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

    class Test2 extends Test1{
        public Test2() {
            this.setName(null);
        }
    }

    @Test
    public void test(){
        final Test2 test2 = new Test2();
        System.out.println(test2.getName());
    }

}
