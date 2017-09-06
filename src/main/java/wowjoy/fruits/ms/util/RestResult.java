package wowjoy.fruits.ms.util;

/**
 * Created by wangziwen on 2017/8/28.
 */
public class RestResult {
    RestResult() {
        this(1000,null,null,true);
    }

    public RestResult(Object data, boolean success) {
        this(1000,null,data,success);
    }

    public RestResult(Integer code, String msg, Object data, boolean success) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = success;
    }

    private Integer code = 1000;
    private String msg;
    private Object data;
    private boolean success = true;

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public RestResult setData(Object data) {
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

    public Object getData() {
        return data;
    }

    public static RestResult getInstance() {
        return new RestResult();
    }
}
