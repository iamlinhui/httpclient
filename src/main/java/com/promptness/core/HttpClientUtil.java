package com.promptness.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;

/**
 * @author Lynn
 */
public class HttpClientUtil {

	private final static Charset UTF_8 = Charset.forName("UTF-8");

	private final CloseableHttpClient httpClient;

	private final RequestConfig requestConfig;

	public HttpClientUtil(CloseableHttpClient httpClient, RequestConfig requestConfig) {
		this.httpClient = httpClient;
		this.requestConfig = requestConfig;
	}
	
	public HttpClientUtil(HttpClientProperties properties) {
		HttpClientAutoConfiguration configuration = new HttpClientAutoConfiguration(properties);
		this.httpClient = configuration.httpClient();
		this.requestConfig = configuration.requestConfig();
	}
	
	public HttpClientUtil() {
		HttpClientAutoConfiguration configuration = new HttpClientAutoConfiguration();
		this.httpClient = configuration.httpClient();
		this.requestConfig = configuration.requestConfig();
	}

	public HttpResult doGet(String url) throws Exception {
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(requestConfig);
		try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
			return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), UTF_8));
		}
	}

	public HttpResult doGet(String url, List<Cookie> cookies) throws Exception {
		HttpGet httpGet = new HttpGet(url);
		setCookies(cookies, httpGet);
		httpGet.setConfig(requestConfig);
		try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
			return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), UTF_8));
		}
	}
	
	public HttpResult doGet(String url, Map<String, String> param) throws Exception {
		return doGet(url, param, null);
	}

	public HttpResult doGet(String url, Map<String, String> param, List<Cookie> cookies) throws Exception {
		// 定义请求的参数
		URIBuilder builder = new URIBuilder(url);
		if (param != null && !param.isEmpty()) {
			param.forEach(builder::setParameter);
		}
		URI uri = builder.build();
		return doGet(uri.toString(), cookies);
	}
	
	public HttpResult doGet(String url,Map<String, String> param, List<Cookie> cookies,FileOutputStream fileOutputStream) throws Exception {
		
		URIBuilder builder = new URIBuilder(url);
		if (param != null && !param.isEmpty()) {
			param.forEach(builder::setParameter);
		}
		
		URI uri = builder.build();
		
		HttpGet httpGet = new HttpGet(uri.toString());
		
		setCookies(cookies, httpGet);
		httpGet.setConfig(requestConfig);
		try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream inputStream = response.getEntity().getContent();
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
				byte[] bs = new byte[1024];
				int len;
				while ((len = inputStream.read(bs)) != -1) {
					bufferedOutputStream.write(bs, 0, len);
				}
				bufferedOutputStream.close();
				inputStream.close();
			}
		}
		
		return HttpResult.SUCCESS;
	}
	
	public HttpResult doPost(String url, Map<String, String> param, List<Cookie> cookies, FileOutputStream fileOutputStream)throws Exception {

		HttpPost httpPost = new HttpPost(url);

		setCookies(cookies, httpPost);
		
		setEntityData(param, httpPost);
		
		httpPost.setConfig(requestConfig);
		try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream inputStream = response.getEntity().getContent();
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
				byte[] bs = new byte[1024];
				int len;
				while ((len = inputStream.read(bs)) != -1) {
					bufferedOutputStream.write(bs, 0, len);
				}
				bufferedOutputStream.close();
				inputStream.close();
				
				return HttpResult.SUCCESS;
			}
			
			return HttpResult.getErrorHttpResult(response.getStatusLine().getStatusCode());
		}
	}

	public HttpResult doPost(String url, List<Cookie> cookies) throws Exception {
		// 创建http POST请求
		return doPost(url, null, cookies);
	}

	public HttpResult doPost(String url, Map<String, String> param) throws Exception {
		// 创建http POST请求
		return doPost(url, param, null);
	}

	public HttpResult doPost(String url, Map<String, String> param, List<Cookie> cookies) throws Exception {
		// 创建http POST请求
		HttpPost httpPost = new HttpPost(url);
		setCookies(cookies, httpPost);

		setEntityData(param, httpPost);
		
		httpPost.setConfig(requestConfig);
		try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
			// 执行请求
			if (response.getEntity() != null) {
				return new HttpResult(response.getStatusLine().getStatusCode(),EntityUtils.toString(response.getEntity(), UTF_8));
			}
			return HttpResult.ENTITY_EMPTY;
		}
		
	}

	public HttpResult doPost(String url) throws Exception {
		return doPost(url, null, null);
	}
	
	public HttpResult doPostFile(String url, Map<String, String> params, List<Cookie> cookies, Map<String, File> files) throws Exception {
		
		HttpPost httpPost = new HttpPost(url);
		
		setCookies(cookies, httpPost);
		
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
		
		if (files != null && !files.isEmpty()) {
			files.forEach((k, v) -> multipartEntityBuilder.addBinaryBody(k, v));
		}
		
		if (params != null && !params.isEmpty()) {
			params.forEach((k, v) -> multipartEntityBuilder.addTextBody(k, v, ContentType.TEXT_PLAIN.withCharset(UTF_8)));
		}
			
		httpPost.setEntity(multipartEntityBuilder.build());
		
		try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
			// 执行请求
			if (response.getEntity() != null) {
				return new HttpResult(response.getStatusLine().getStatusCode(),EntityUtils.toString(response.getEntity(), UTF_8));
			}
			return HttpResult.ENTITY_EMPTY;
		}
		
	}
	
	

	public HttpResult doPostJson(String url, Map<String, String> param) throws Exception {
		// 创建http POST请求
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(this.requestConfig);
		if (param != null) {
			// 构造一个字符串的实体
			StringEntity stringEntity = new StringEntity(JSON.toJSONString(param), ContentType.APPLICATION_JSON);
			// 将请求实体设置到httpPost对象中
			httpPost.setEntity(stringEntity);
		}
		try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
			// 执行请求
			if (response.getEntity() != null) {
				return new HttpResult(response.getStatusLine().getStatusCode(),EntityUtils.toString(response.getEntity(), UTF_8));
			}
			return HttpResult.ENTITY_EMPTY;
		}
	}

	public <T> T doPostJson(String url, Map<String, String> param, Class<T> clazz) throws Exception {
		// 创建http POST请求
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(this.requestConfig);
		if (param != null) {
			// 构造一个字符串的实体
			StringEntity stringEntity = new StringEntity(JSON.toJSONString(param), ContentType.APPLICATION_JSON);
			// 将请求实体设置到httpPost对象中
			httpPost.setEntity(stringEntity);
		}
		try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
			// 执行请求
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && response.getEntity() != null) {
				return JSON.parseObject(EntityUtils.toString(response.getEntity(), UTF_8), clazz);
			}
		}
		return null;
	}

	public HttpResult doPostJson(String url, Map<String, String> param, List<Cookie> cookies) throws Exception {
		// 创建http POST请求
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(this.requestConfig);
		setCookies(cookies, httpPost);
		if (param != null) {
			// 构造一个字符串的实体
			StringEntity stringEntity = new StringEntity(JSON.toJSONString(param), ContentType.APPLICATION_JSON);
			// 将请求实体设置到httpPost对象中
			httpPost.setEntity(stringEntity);
		}
		try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
			// 执行请求
			if (response.getEntity() != null) {
				return new HttpResult(response.getStatusLine().getStatusCode(),EntityUtils.toString(response.getEntity(), UTF_8));
			}
			return HttpResult.ENTITY_EMPTY;
		}
	}



	public HttpResult doPut(String url, Map<String, String> param) throws IOException {
		// 创建http POST请求
		HttpPut httpPut = new HttpPut(url);
		httpPut.setConfig(this.requestConfig);
		
		setEntityData(param, httpPut);
		
		try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
			// 执行请求
			if (response.getEntity() != null) {
				return new HttpResult(response.getStatusLine().getStatusCode(),EntityUtils.toString(response.getEntity(), UTF_8));
			}
			return HttpResult.ENTITY_EMPTY;
		}
	}

	public HttpResult doPut(String url) throws Exception {
		return this.doPut(url, null);
	}

	public HttpResult doDelete(String url, Map<String, String> param) throws Exception {
		param.put("_method", "DELETE");
		return this.doPost(url, param);
	}

	public HttpResult doDelete(String url) throws IOException {
		// 创建http DELETE请求
		HttpDelete httpDelete = new HttpDelete(url);
		httpDelete.setConfig(this.requestConfig);
		try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
			// 执行请求
			if (response.getEntity() != null) {
				return new HttpResult(response.getStatusLine().getStatusCode(),EntityUtils.toString(response.getEntity(), UTF_8));
			}
			return HttpResult.ENTITY_EMPTY;
		}
	}

	/**
	 * 设置Entity数据
	 *
	 * @param param                          参数
	 * @param httpEntityEnclosingRequestBase httpclient
	 */
	private void setEntityData(Map<String, String> param,HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase) {
		if (param == null || param.isEmpty()) {
			return;
		}
		List<NameValuePair> parameters = new ArrayList<>();
		param.forEach((k, v) -> parameters.add(new BasicNameValuePair(k, v)));
		// 构造一个form表单式的实体
		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, UTF_8);
		// 将请求实体设置到httpPost对象中
		httpEntityEnclosingRequestBase.setEntity(formEntity);
	}

	private void setCookies(List<Cookie> cookies, HttpRequestBase httpRequestBase) {
		if (cookies == null || cookies.isEmpty()) {
			return;
		}
		StringBuilder cookieStr = new StringBuilder();
		cookies.forEach(cookie -> cookieStr.append("; ").append(cookie.getName()).append("=").append(cookie.getValue()));
		httpRequestBase.setHeader("Cookie", cookieStr.substring(2));

	}
}
