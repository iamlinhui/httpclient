package com.promptness.core;

import com.alibaba.fastjson.JSON;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 返回前台数据类型
 *
 * @author Lynn
 */
@Data
@Accessors(chain = true)
public class HttpResult {

	public final static HttpResult DEFAULT = new HttpResult(0, "DEFAULT");

	public final static HttpResult ERROR = new HttpResult(500, "ERROR");

	public final static HttpResult SUCCESS = new HttpResult(200, "SUCCESS");

	public final static HttpResult ENTITY_EMPTY = new HttpResult(204, "ENTITY_EMPTY");

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

}