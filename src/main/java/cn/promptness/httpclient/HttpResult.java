package cn.promptness.httpclient;

import com.google.gson.Gson;
import org.apache.http.Header;
import java.lang.reflect.Type;
import java.util.Arrays;

public class HttpResult {

    public static final HttpResult ERROR = new HttpResult(500, "ERROR");
    public static final HttpResult SUCCESS = new HttpResult(200, "SUCCESS");

    private int code;
    private String message;
    private Header[] headers;

    public HttpResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public HttpResult(int code, String message, Header[] headers) {
        this.code = code;
        this.message = message;
        this.headers = headers;
    }

    public static HttpResult getErrorHttpResult(String message) {
        return new HttpResult(500, message);
    }

    public boolean isSuccess() {
        return this.code == 200;
    }

    public <T> T getContent(Class<T> clazz) {
        return new Gson().fromJson(this.message, clazz);
    }

    public <T> T getContent(Type type) {
        return new Gson().fromJson(this.message, type);
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public Header[] getHeaders() { return headers; }

    @Override
    public String toString() {
        return "HttpResult{code=" + code + ", message='" + message + "', headers=" + Arrays.toString(headers) + '}';
    }
}
