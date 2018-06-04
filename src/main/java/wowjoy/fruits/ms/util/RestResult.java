package wowjoy.fruits.ms.util;

import java.util.function.Supplier;

/**
 * Created by wangziwen on 2017/8/28.
 */
public class RestResult<T> {

    public RestResult(Integer code, String msg, T data, boolean success) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = success;
    }

    private Integer code = 200;
    private String msg;
    private String key;
    private T data;
    private boolean success = true;

    public void setKey(String key) {
        this.key = key;
    }

    public RestResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public RestResult setData(T data) {
        this.data = data;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public boolean getSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public static <T> RestResult<T> newSuccess() {
        return new RestResult<>(200, null, null, true);
    }

    public static RestResult newError(int code, String msg) {
        return new RestResult<>(code, msg, null, false);
    }

    public static <T> RestResult<T> newError(int code, String key, String msg) {
        RestResult<T> restResult = new RestResult<>(code, msg, null, false);
        restResult.setKey(key);
        return restResult;
    }
    public static <T> RestResult<T> newError(int code, String msg, Supplier<T> data) {
        return new RestResult<>(code, msg, data.get(), false);
    }
}
