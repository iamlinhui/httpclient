package cn.promptness.httpclient.core;

import cn.promptness.httpclient.HttpResult;
import org.apache.http.Header;
import org.apache.http.cookie.Cookie;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 链式请求构建器
 */
public class HttpRequest {

    private final DefaultHttpClient client;
    private final String url;
    private final String method;

    private Map<String, String> headers;
    private Map<String, String> cookies;
    private Map<String, String> params;
    private Map<String, File> files;
    private Object jsonBody;

    protected HttpRequest(DefaultHttpClient client, String url, String method) {
        this.client = client;
        this.url = url;
        this.method = method;
    }

    // --- Builder Methods ---
    public HttpRequest addHeader(Header header) {
        return addHeader(header.getName(), header.getValue());
    }

    public HttpRequest addHeaders(List<Header> headers) {
        headers.forEach(this::addHeader);
        return this;
    }

    public HttpRequest addHeader(String name, String value) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.put(name, value);
        return this;
    }

    public HttpRequest addHeaders(Map<String, String> headers) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.putAll(headers);
        return this;
    }

    public HttpRequest addParam(String name, String value) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.put(name, value);
        return this;
    }

    public HttpRequest addParams(Map<String, String> params) {
        if (params != null) {
            if (this.params == null) {
                this.params = new HashMap<>();
            }
            this.params.putAll(params);
        }
        return this;
    }

    public HttpRequest addCookie(Cookie cookie) {
        return addCookie(cookie.getName(), cookie.getValue());
    }

    public HttpRequest addCookies(List<Cookie> cookies) {
        cookies.forEach(this::addCookie);
        return this;
    }

    public HttpRequest addCookie(String name, String value) {
        if (this.cookies == null) {
            this.cookies = new HashMap<>();
        }
        this.cookies.put(name, value);
        return this;
    }

    public HttpRequest addCookies(Map<String, String> cookies) {
        if (this.cookies == null) {
            this.cookies = new HashMap<>();
        }
        this.cookies.putAll(cookies);
        return this;
    }

    public HttpRequest addFile(String key, File file) {
        if (this.files == null) {
            this.files = new HashMap<>();
        }
        this.files.put(key, file);
        return this;
    }

    public HttpRequest setJsonBody(Object body) {
        this.jsonBody = body;
        return this;
    }

    // --- Execute Methods ---

    public HttpResult execute() {
        return client.doExecute(this);
    }

    public <T> T execute(Class<T> clazz) {
        HttpResult result = execute();
        if (result.isSuccess()) {
            return result.getContent(clazz);
        }
        throw new RuntimeException("Request failed: " + result.getCode() + " " + result.getMessage());
    }

    public <T> T execute(Type type) {
        HttpResult result = execute();
        if (result.isSuccess()) {
            return result.getContent(type);
        }
        throw new RuntimeException("Request failed: " + result.getCode());
    }

    /**
     * 下载文件到指定的流
     * 注意：方法内部会自动 flush 和 close 传入的流 (遵循原代码逻辑)
     */
    public HttpResult download(OutputStream outputStream) {
        return client.doDownload(this, outputStream);
    }

    /**
     * 便捷方法：下载文件到本地路径
     */
    public HttpResult download(File targetFile) {
        // 确保父目录存在
        if (targetFile.getParentFile() != null && !targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        try (OutputStream os = Files.newOutputStream(targetFile.toPath())) {
            return download(os);
        } catch (IOException e) {
            return HttpResult.getErrorHttpResult("File create failed: " + e.getMessage());
        }
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public Map<String, File> getFiles() {
        return files;
    }

    public Object getJsonBody() {
        return jsonBody;
    }
}
