package wowjoy.fruits.ms.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import wowjoy.fruits.ms.module.AbstractEntity;

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

    public static <T extends AbstractEntity> T toT(AbstractEntity entity, Class<T> tClass) {
        return newGson().fromJson(newGson().toJsonTree(entity), TypeToken.of(tClass).getType());
    }
}
