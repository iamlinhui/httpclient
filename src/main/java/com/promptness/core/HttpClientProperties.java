package com.promptness.core;

import lombok.Data;

/**
 * @author Lynn
 */
@Data
public class HttpClientProperties {

    private String agent = "agent";
    private Integer maxConnTotal = 200;
    private Integer maxConnPerRoute = 50;
    private Integer connectTimeOut = 10000;
    private Integer connectionRequestTimeout = 10000;
    private Integer socketTimeOut = 50000;
    private Integer validateAfterInactivity = 1000;
    private Boolean expectContinueEnabled = true;

}
