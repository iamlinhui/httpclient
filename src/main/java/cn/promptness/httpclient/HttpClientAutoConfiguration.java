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
import java.util.*;
import java.util.function.Consumer;


/**
 * @author Lynn
 */
public class HttpClientAutoConfiguration {

    private static final Map<String, Map<String, String>> LOCAL_IP_CACHE = new LinkedHashMap<>();

    private final HttpClientProperties properties;

    public HttpClientAutoConfiguration(HttpClientProperties properties) {
        this.properties = properties;
    }

    public HttpClientAutoConfiguration() {
        this.properties = new HttpClientProperties();
    }

    /*
     * 在打成jar包时,使用ClassPathResource的getFile或者getPath之类的方式是不能获得结果的,但是可以使用classPathResource.getInputStream直接读取到
     * class.getClassLoader().getResource()直接从resources目录下找,用的是相对路径,文件名（参数）前面不用加/
     * class.getResource()是以resources为根目录的绝对路径,文件名（参数）前面需要加/
     *
     * @author lynn
     * @date 2022/1/7 11:22
     * @since v1.0.0
     */
    static {
        Optional.ofNullable(ClassLoader.getSystemResourceAsStream("ip.yml")).ifPresent(resource -> LOCAL_IP_CACHE.putAll(new Yaml().load(resource)));
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
                    String ip = Optional.ofNullable(LOCAL_IP_CACHE.get(properties.getIpLabel())).orElse(new LinkedHashMap<>()).get(host);
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
