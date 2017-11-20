import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.junit.Test;

import java.util.LinkedHashMap;

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
    public void test() {
    }

}
