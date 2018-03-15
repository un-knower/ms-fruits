package wowjoy.fruits.ms.module;

import java.util.UUID;

/**
 * Created by wangziwen on 2018/3/9.
 */
public interface EntityUtils {
    default String obtainUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
