package wowjoy.fruits.ms.util;

/**
 * Created by wangziwen on 2017/8/28.
 */
public class RestResult<T> {
    RestResult() {
        this(1000, null, null, true);
    }

    public RestResult(T data, boolean success) {
        this(1000, null, data, success);
    }

    public RestResult(Integer code, String msg, T data, boolean success) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = success;
    }

    private Integer code = 1000;
    private String msg;
    private T data;
    private boolean success = true;

    public void setCode(Integer code) {
        this.code = code;
    }

    public RestResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public RestResult setData(T data) {
        this.data = data;
        return this;
    }

    public RestResult setSuccess(boolean success) {
        this.success = success;
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

    public static <T> RestResult<T> getInstance() {
        return new RestResult<>();
    }

    public static <T> RestResultPage<T> newPage(int pageNum, int pageSize, long total, T data) {
        return new RestResultPage<>(pageNum, pageSize, total, data);
    }

    public static RestResult newError(int code, String msg) {
        return new RestResult(code, msg, null, false);
    }
}
