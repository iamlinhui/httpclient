package cn.promptness.httpclient.config;

import cn.promptness.httpclient.HttpClientProperties;
import cn.promptness.httpclient.dns.YamlDnsResolver;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.Arrays;
import java.util.Collections;

public class HttpClientFactory {

    public static CloseableHttpClient createClient(HttpClientProperties properties) {
        return HttpClientBuilder.create()
                .setUserAgent(properties.getAgent())
                .setConnectionManager(createConnectionManager(properties))
                // 默认不复用连接，如有性能需求可自定义策略
                .setConnectionReuseStrategy(new NoConnectionReuseStrategy())
                .setDefaultRequestConfig(createRequestConfig(properties))
                .build();
    }

    private static PoolingHttpClientConnectionManager createConnectionManager(HttpClientProperties properties) {
        // 注入自定义 DNS 解析器
        YamlDnsResolver dnsResolver = new YamlDnsResolver("ip.yml", properties.getIpLabel());
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", SSLConnectionSocketFactory.getSocketFactory())
                        .build(),
                dnsResolver
        );
        manager.setMaxTotal(properties.getMaxConnTotal());
        manager.setDefaultMaxPerRoute(properties.getMaxConnPerRoute());
        manager.setValidateAfterInactivity(properties.getValidateAfterInactivity());
        return manager;
    }

    private static RequestConfig createRequestConfig(HttpClientProperties properties) {
        return RequestConfig.custom()
                .setConnectTimeout(properties.getConnectTimeOut())
                .setSocketTimeout(properties.getSocketTimeOut())
                .setConnectionRequestTimeout(properties.getConnectionRequestTimeout())
                .setCookieSpec(properties.getCookieSpecs())
                .setExpectContinueEnabled(properties.getExpectContinueEnabled())
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Collections.singletonList(AuthSchemes.BASIC))
                .setRedirectsEnabled(properties.getRedirectsEnabled())
                .build();
    }
}
