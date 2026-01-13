package cn.promptness.httpclient;

import com.google.gson.Gson;
import org.apache.http.Header;

import java.lang.reflect.Type;
import java.util.List;

public class HttpResult {

    public static final HttpResult ERROR = new HttpResult(500, "ERROR");
    public static final HttpResult SUCCESS = new HttpResult(200, "SUCCESS");

    private final int code;
    private final String message;
    private List<Header> headers;

    public HttpResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public HttpResult(int code, String message, List<Header> headers) {
        this.code = code;
        this.message = message;
        this.headers = headers;
    }

    public static HttpResult getErrorHttpResult(String message) {
        return new HttpResult(ERROR.code, message);
    }

    public boolean isSuccess() {
        return this.code == SUCCESS.code;
    }

    public <T> T getContent(Class<T> clazz) {
        return new Gson().fromJson(this.message, clazz);
    }

    public <T> T getContent(Type type) {
        return new Gson().fromJson(this.message, type);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "HttpResult{code=" + code + ", message='" + message + "', headers=" + headers + '}';
    }
}
