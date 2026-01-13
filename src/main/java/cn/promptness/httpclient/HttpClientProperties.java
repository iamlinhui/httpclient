package cn.promptness.httpclient;

import org.apache.http.client.config.CookieSpecs;

public class HttpClientProperties {
    /**
     * User-Agent 标识
     */
    private String agent = "Mozilla/5.0";
    /**
     * 连接池最大连接数
     */
    private Integer maxConnTotal = 200;
    /**
     * 每个路由的最大连接数
     */
    private Integer maxConnPerRoute = 50;
    /**
     * 连接超时时间 (毫秒)
     */
    private Integer connectTimeOut = 10000;
    /**
     * 从连接池获取连接的超时时间 (毫秒)
     */
    private Integer connectionRequestTimeout = 10000;
    /**
     * 读取数据超时时间 (毫秒)
     */
    private Integer socketTimeOut = 50000;
    /**
     * 空闲连接校验时间
     */
    private Integer validateAfterInactivity = 1000;
    /**
     * 是否启用 100-continue 机制
     */
    private Boolean expectContinueEnabled = true;
    /**
     * 不自动处理 Set-Cookie 头，也不发送 Cookie
     */
    private String cookieSpecs = CookieSpecs.IGNORE_COOKIES;
    /**
     * 是否启用重定向 (默认禁用重定向)
     */
    private boolean redirectsEnabled = false;
    /**
     * 本地 DNS 映射标签
     */
    private String ipLabel = "default";
    /**
     * 是否忽略 SSL 证书校验 (默认 false，生产环境建议 false，测试环境可设为 true)
     */
    private boolean sslIgnore = false;

    public boolean isSslIgnore() {
        return sslIgnore;
    }

    public void setSslIgnore(boolean sslIgnore) {
        this.sslIgnore = sslIgnore;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public Integer getMaxConnTotal() {
        return maxConnTotal;
    }

    public void setMaxConnTotal(Integer maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
    }

    public Integer getMaxConnPerRoute() {
        return maxConnPerRoute;
    }

    public void setMaxConnPerRoute(Integer maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
    }

    public Integer getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(Integer connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public Integer getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(Integer connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public Integer getSocketTimeOut() {
        return socketTimeOut;
    }

    public void setSocketTimeOut(Integer socketTimeOut) {
        this.socketTimeOut = socketTimeOut;
    }

    public Integer getValidateAfterInactivity() {
        return validateAfterInactivity;
    }

    public void setValidateAfterInactivity(Integer validateAfterInactivity) {
        this.validateAfterInactivity = validateAfterInactivity;
    }

    public Boolean getExpectContinueEnabled() {
        return expectContinueEnabled;
    }

    public void setExpectContinueEnabled(Boolean expectContinueEnabled) {
        this.expectContinueEnabled = expectContinueEnabled;
    }

    public String getCookieSpecs() {
        return cookieSpecs;
    }

    public void setCookieSpecs(String cookieSpecs) {
        this.cookieSpecs = cookieSpecs;
    }

    public boolean getRedirectsEnabled() {
        return redirectsEnabled;
    }

    public void setRedirectsEnabled(boolean redirectsEnabled) {
        this.redirectsEnabled = redirectsEnabled;
    }

    public String getIpLabel() {
        return ipLabel;
    }

    public void setIpLabel(String ipLabel) {
        this.ipLabel = ipLabel;
    }
}
