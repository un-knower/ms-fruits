import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;
import wowjoy.fruits.ms.module.user.FruitUser;

import java.util.regex.Pattern;

/**
 * Created by wangziwen on 2017/9/15.
 */
public class PatternTest {
    @Test
    public void test() throws Exception {
        JsonElement jsonElement = new Gson().toJsonTree(null);
        System.out.println(jsonElement.isJsonArray());
        System.out.println(jsonElement.isJsonNull());
        System.out.println(jsonElement.isJsonObject());
        System.out.println(jsonElement.isJsonPrimitive());
    }


}
