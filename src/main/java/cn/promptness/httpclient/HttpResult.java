package cn.promptness.httpclient;

import com.google.gson.Gson;
import org.apache.http.Header;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
        return new HttpResult(ERROR.code, message);
    }

    public static HttpResult getErrorHttpResult(int code) {
        return new HttpResult(code, ERROR.message);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
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
        return new Gson().fromJson(this.getMessage(), clazz);
    }

    /**
     * new TypeToken<List<Student>>(){}.getType();
     *
     * @author lynn
     * @date 2021/7/5 15:55
     * @since v1.0.0
     */
    public <T> T getContent(Type typeOfT) {
        return new Gson().fromJson(this.getMessage(), typeOfT);
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public List<Header> getHeaderList(final String name) {
        List<Header> headersFound = new ArrayList<>();
        if (headers == null || headers.length == 0) {
            return headersFound;
        }
        for (final Header header : this.headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                headersFound.add(header);
            }
        }
        return headersFound;
    }
}
