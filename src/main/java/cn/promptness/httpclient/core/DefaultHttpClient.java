package cn.promptness.httpclient.core;

import cn.promptness.httpclient.HttpClientProperties;
import cn.promptness.httpclient.HttpResult;
import cn.promptness.httpclient.config.HttpClientFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HTTP 客户端核心实现
 */
public class DefaultHttpClient {

    private final CloseableHttpClient httpClient;
    private final Gson gson;

    public DefaultHttpClient() {
        this(new HttpClientProperties());
    }

    public DefaultHttpClient(HttpClientProperties properties) {
        this.httpClient = HttpClientFactory.createClient(properties);
        this.gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    public HttpRequest get(String url) {
        return new HttpRequest(this, url, "GET");
    }

    public HttpRequest post(String url) {
        return new HttpRequest(this, url, "POST");
    }

    public HttpRequest put(String url) {
        return new HttpRequest(this, url, "PUT");
    }

    public HttpRequest delete(String url) {
        return new HttpRequest(this, url, "DELETE");
    }

    protected HttpResult doExecute(HttpRequest request) {
        try {
            HttpRequestBase httpMethod = buildHttpMethod(request);
            try (CloseableHttpResponse response = httpClient.execute(httpMethod)) {
                String content = null;
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    content = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                }
                return new HttpResult(response.getStatusLine().getStatusCode(), content, Arrays.asList(response.getAllHeaders()));
            }
        } catch (Exception e) {
            return HttpResult.getErrorHttpResult(e.getMessage());
        }
    }

    protected HttpResult doDownload(HttpRequest request, OutputStream outputStream) {
        try {
            HttpRequestBase httpMethod = buildHttpMethod(request);
            try (CloseableHttpResponse response = httpClient.execute(httpMethod)) {
                int statusCode = response.getStatusLine().getStatusCode();
                // 只有 200 OK 才写入流
                if (statusCode == 200 && response.getEntity() != null) {
                    try (InputStream is = response.getEntity().getContent(); BufferedOutputStream bos = new BufferedOutputStream(outputStream)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = is.read(buffer)) != -1) {
                            bos.write(buffer, 0, len);
                        }
                        bos.flush(); // 确保写出
                    }
                    return HttpResult.SUCCESS;
                } else {
                    return new HttpResult(statusCode, "Download failed, status: " + statusCode);
                }
            }
        } catch (Exception e) {
            return HttpResult.getErrorHttpResult("Download Error: " + e.getMessage());
        }
    }

    private HttpRequestBase buildHttpMethod(HttpRequest req) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(req.getUrl());
        String method = req.getMethod().toUpperCase();

        boolean paramsInUrl = "GET".equals(method) || "DELETE".equals(method) || req.getJsonBody() != null || (req.getFiles() != null && !req.getFiles().isEmpty());

        if (paramsInUrl && req.getParams() != null) {
            req.getParams().forEach(uriBuilder::setParameter);
        }

        HttpRequestBase httpRequest;
        switch (method) {
            case "POST":
                httpRequest = new HttpPost(uriBuilder.build());
                break;
            case "PUT":
                httpRequest = new HttpPut(uriBuilder.build());
                break;
            case "DELETE":
                httpRequest = new HttpDelete(uriBuilder.build());
                break;
            default:
                httpRequest = new HttpGet(uriBuilder.build());
                break;
        }

        if (req.getHeaders() != null) {
            req.getHeaders().forEach(httpRequest::setHeader);
        }

        if (req.getCookies() != null && !req.getCookies().isEmpty()) {
            String cookie = req.getCookies().entrySet().stream().map(c -> c.getKey() + "=" + c.getValue()).collect(Collectors.joining("; "));
            httpRequest.setHeader("Cookie", cookie);
        }

        if (httpRequest instanceof HttpEntityEnclosingRequestBase) {
            HttpEntityEnclosingRequestBase entityRequest = (HttpEntityEnclosingRequestBase) httpRequest;

            if (req.getFiles() != null && !req.getFiles().isEmpty()) {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                req.getFiles().forEach(builder::addBinaryBody);
                if (req.getParams() != null && !paramsInUrl) {
                    req.getParams().forEach((k, v) -> builder.addTextBody(k, v, ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8)));
                }
                entityRequest.setEntity(builder.build());
            } else if (req.getJsonBody() != null) {
                String json = gson.toJson(req.getJsonBody());
                entityRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
            } else if (!paramsInUrl && req.getParams() != null) {
                List<NameValuePair> formParams = new ArrayList<>();
                req.getParams().forEach((k, v) -> formParams.add(new BasicNameValuePair(k, v)));
                entityRequest.setEntity(new UrlEncodedFormEntity(formParams, StandardCharsets.UTF_8));
            }
        }
        return httpRequest;
    }
}
