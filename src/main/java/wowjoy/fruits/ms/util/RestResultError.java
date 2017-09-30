package wowjoy.fruits.ms.util;

/**
 * Created by wangziwen on 2017/9/29.
 */
public class RestResultError extends RestResult {
    public RestResultError(String msg) {
        super(5000, msg, null, false);
    }
}
