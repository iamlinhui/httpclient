package cn.promptness.httpclient;

import org.apache.commons.lang3.StringUtils;
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
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;


/**
 * @author Lynn
 */
public class HttpClientAutoConfiguration {

    private static final Map<String, String> LOCAL_IP_CACHE;

    private final HttpClientProperties properties;

    public HttpClientAutoConfiguration(HttpClientProperties properties) {
        this.properties = properties;
    }

    public HttpClientAutoConfiguration() {
        this.properties = new HttpClientProperties();
    }

    static {
        InputStream resource = ClassLoader.getSystemResourceAsStream("ip.yaml");
        LOCAL_IP_CACHE = new Yaml().loadAs(resource, Map.class);
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
                .setCookieSpec(properties.getCookieSpecs()).setExpectContinueEnabled(properties.getExpectContinueEnabled())
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Collections.singletonList(AuthSchemes.BASIC))
                .setRedirectsEnabled(properties.getRedirectsEnabled()).build();
    }

    /**
     * 定义连接管理器
     *
     * @return PoolingHttpClientConnectionManager
     */
    private PoolingHttpClientConnectionManager httpClientConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", SSLConnectionSocketFactory.getSocketFactory())
                        .build(),
                host -> {
                    String ip = LOCAL_IP_CACHE.get(host);
                    if (StringUtils.isBlank(ip)) {
                        return InetAddress.getAllByName(host);
                    }
                    return new InetAddress[]{InetAddress.getByAddress(host, ipToByte(ip))};
                });
        connectionManager.setMaxTotal(properties.getMaxConnTotal());
        connectionManager.setDefaultMaxPerRoute(properties.getMaxConnPerRoute());
        connectionManager.setValidateAfterInactivity(properties.getValidateAfterInactivity());
        return connectionManager;
    }

    private byte[] ipToByte(String ip) {
        String[] split = StringUtils.split(ip, '.');
        byte[] result = new byte[4];
        for (int i = 0; i < split.length; i++) {
            result[i] = (byte) (Integer.parseInt(split[i]) & 0xFF);
        }
        return result;
    }
}
