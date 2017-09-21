import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Created by wangziwen on 2017/9/15.
 */
public class PatternTest {
    @Test
    public void test() throws Exception {
        final boolean matches = Pattern.matches("/static/\\w{0,}", "/static/sssss");
        System.out.println(matches);
    }

}
