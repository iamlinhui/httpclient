# HttpClient

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Java](https://img.shields.io/badge/Java-8%2B-blue)
![License](https://img.shields.io/badge/license-MIT-green)

一个基于 Apache HttpClient 封装的轻量级、链式调用、支持泛型和文件下载的 Java HTTP 客户端。

## ✨ 特性

- 🚀 **链式调用 (Fluent API)**：告别繁琐的参数列表。
- 📦 **自动泛型解析**：直接返回 Java Bean，无需手动处理 JSON。
- 📂 **文件下载/上传**：支持流式下载和 Multipart 上传。
- 🛡️ **SSL 忽略**：支持开发环境跳过 SSL 证书校验。
- ⚙️ **配置灵活**：支持自定义 DNS (hosts)、连接池参数。

## 🛠️ 快速开始

### 1. 引入依赖
```xml
<dependency>
    <groupId>cn.promptness</groupId>
    <artifactId>httpclient</artifactId>
    <version>2.0.0</version>
</dependency>

```

### 2. 发送请求

**GET 请求：**

```java
DefaultHttpClient client = new DefaultHttpClient();
// 直接获取对象
User user = client.get("http://api.example.com/users/1")
                  .addParam("active", "true")
                  .execute(User.class);

```

**POST JSON 请求：**

```java
client.post("http://api.example.com/users")
      .addHeader("Authorization", "Bearer token")
      .setJsonBody(new UserDTO("Tom", 18))
      .execute();

```

**文件下载：**

```java
client.get("http://example.com/file.zip")
      .download(new File("/tmp/file.zip"));

```

## ⚙️ 高级配置

```java
HttpClientProperties props = new HttpClientProperties();
props.setConnectTimeOut(5000);
props.setSslIgnore(true); // 忽略 HTTPS 证书校验
props.setIpLabel("dev");  // 使用 ip.yml 中的 dev 环境 host 配置

DefaultHttpClient client = new DefaultHttpClient(props);

```

```yaml
stable:
  api.example.com: 10.9.1.2
prod:
  api.example.com: 10.9.2.3
```
