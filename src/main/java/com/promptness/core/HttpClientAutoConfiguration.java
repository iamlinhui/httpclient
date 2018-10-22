package com.promptness.core;

import java.util.Arrays;

import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.google.common.collect.ImmutableList;

/**
 * @author Lynn
 */
public class HttpClientAutoConfiguration {

	private HttpClientProperties properties;
	
	public HttpClientAutoConfiguration(HttpClientProperties properties) {
		this.properties = properties;
	}
	
	public HttpClientAutoConfiguration() {
		this.properties = new HttpClientProperties();
	}

	/**
	 * httpclient bean 的定义
	 *
	 * @return CloseableHttpClient
	 */
	public CloseableHttpClient httpClient() {
		return HttpClientBuilder.create().setUserAgent(properties.getAgent())
				.setConnectionManager(httpClientConnectionManager())
				.setConnectionReuseStrategy(new NoConnectionReuseStrategy()).build();
	}

	public RequestConfig requestConfig() {
		// 构建requestConfig
		return RequestConfig.custom().setConnectTimeout(properties.getConnectTimeOut())
				.setSocketTimeout(properties.getSocketTimeOut())
				.setConnectionRequestTimeout(properties.getConnectionRequestTimeout())
				.setCookieSpec(CookieSpecs.DEFAULT).setExpectContinueEnabled(properties.getExpectContinueEnabled())
				.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
				.setProxyPreferredAuthSchemes(ImmutableList.of(AuthSchemes.BASIC)).build();
	}

	/**
	 * 定义连接管理器
	 *
	 * @return PoolingHttpClientConnectionManager
	 */
	private PoolingHttpClientConnectionManager httpClientConnectionManager() {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(properties.getMaxConnTotal());
		connectionManager.setDefaultMaxPerRoute(properties.getMaxConnPerRoute());
		connectionManager.setValidateAfterInactivity(properties.getValidateAfterInactivity());
		return connectionManager;
	}

}
