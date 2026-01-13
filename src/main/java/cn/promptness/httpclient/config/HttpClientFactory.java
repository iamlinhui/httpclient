package cn.promptness.httpclient.config;

import cn.promptness.httpclient.HttpClientProperties;
import cn.promptness.httpclient.dns.YamlDnsResolver;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
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
        // 配置 SSL 证书校验 注册协议
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", properties.isSslIgnore() ? createUnsafeSslFactory() : SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, dnsResolver);
        manager.setMaxTotal(properties.getMaxConnTotal());
        manager.setDefaultMaxPerRoute(properties.getMaxConnPerRoute());
        manager.setValidateAfterInactivity(properties.getValidateAfterInactivity());
        return manager;
    }

    /**
     * 创建一个信任所有证书的 SSL 工厂 (用于解决 PKIX path validation failed)
     */
    private static SSLConnectionSocketFactory createUnsafeSslFactory() {
        try {
            // 信任所有证书
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, (chain, authType) -> true).build();
            // NoopHostnameVerifier 意味着不校验域名与证书是否匹配
            return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new RuntimeException("Failed to create unsafe SSL factory", e);
        }
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
