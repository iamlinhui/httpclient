package cn.promptness.httpclient;

import org.apache.http.client.config.CookieSpecs;

/**
 * @author Lynn
 */
public class HttpClientProperties {

    private String agent = "agent";
    private Integer maxConnTotal = 200;
    private Integer maxConnPerRoute = 50;
    private Integer connectTimeOut = 10000;
    private Integer connectionRequestTimeout = 10000;
    private Integer socketTimeOut = 50000;
    private Integer validateAfterInactivity = 1000;
    private Boolean expectContinueEnabled = true;
    private String cookieSpecs = CookieSpecs.IGNORE_COOKIES;
    private boolean redirectsEnabled = true;

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
}
