package wowjoy.fruits.ms.util;

import com.google.gson.Gson;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public class GsonUtils {
    private static Gson gson;

    public static Gson newGson() {
        if (gson == null)
            gson = new Gson();
        return gson;
    }
}
