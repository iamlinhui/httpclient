package cn.promptness.core;

import com.alibaba.fastjson.JSON;

/**
 * 返回前台数据类型
 *
 * @author linhuid
 */
public class HttpResult {

    public static final HttpResult DEFAULT = new HttpResult(0, "DEFAULT");

    public static final HttpResult ERROR = new HttpResult(500, "ERROR");

    public static final HttpResult SUCCESS = new HttpResult(200, "SUCCESS");

    public static final HttpResult ENTITY_EMPTY = new HttpResult(204, "ENTITY_EMPTY");

    private int code;
    private String message;

    public HttpResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static HttpResult getErrorHttpResult(String message) {
        return new HttpResult(ERROR.code, message);
    }

    public static HttpResult getErrorHttpResult(int code) {
        return new HttpResult(code, ERROR.message);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return this.code == SUCCESS.code;
    }

    public boolean isFailed() {
        return !isSuccess();
    }

    public <T> T getContent(Class<T> clazz) {
        return JSON.parseObject(this.getMessage(), clazz);
    }


}
