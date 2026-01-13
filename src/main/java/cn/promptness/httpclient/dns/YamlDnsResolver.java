package cn.promptness.httpclient.dns;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.DnsResolver;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * 基于 YAML 文件的本地 DNS 解析器
 */
public class YamlDnsResolver implements DnsResolver {

    private final Map<String, Map<String, String>> ipCache;
    private final String currentLabel;

    /**
     * @param yamlFileName resources 下的文件名
     * @param currentLabel 当前环境标签 (如 "default")
     */
    public YamlDnsResolver(String yamlFileName, String currentLabel) {
        this.currentLabel = currentLabel;
        // 加载配置文件
        try (InputStream in = ClassLoader.getSystemResourceAsStream(yamlFileName)) {
            if (in != null) {
                this.ipCache = new Yaml().load(in);
            } else {
                this.ipCache = null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load DNS config file: " + yamlFileName, e);
        }
    }

    @Override
    public InetAddress[] resolve(String host) throws UnknownHostException {
        // 尝试从本地缓存获取 IP
        if (ipCache != null && ipCache.containsKey(currentLabel)) {
            String ip = ipCache.get(currentLabel).get(host);
            if (StringUtils.isNotBlank(ip)) {
                return new InetAddress[]{InetAddress.getByAddress(host, ipToByte(ip))};
            }
        }
        // 默认走系统 DNS
        return InetAddress.getAllByName(host);
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
