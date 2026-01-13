package cn.promptness.httpclient;

import cn.promptness.httpclient.core.DefaultHttpClient;

import java.io.File;

public class HttpClientTest {

    public static class User {

        private String name;

        private Integer age;

        public User() {
        }

        public User(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }


    public static void main(String[] args) {
        // 1. 初始化客户端 (单例模式)
        DefaultHttpClient client = new DefaultHttpClient();

        // 2. 简单 GET 请求
        User user = client.get("http://localhost:8080/user/1")
                .execute(User.class);

        // 3. 复杂 POST JSON 请求
        client.post("http://localhost:8080/user/create")
                .addHeader("Authorization", "Bearer token")
                .setJsonBody(new User("Tom", 20))
                .execute();

        // 4. 文件上传
        client.post("http://localhost:8080/upload")
                .addFile("file", new File("test.jpg"))
                .addParam("description", "avatar")
                .execute();


        // 方式 5: 直接下载保存为文件
        client.get("https://example.com/logo.png")
                .download(new File("D:/downloads/logo.png"));

        // 方式 6: 结合参数下载
        client.get("https://example.com/api/report")
                .addParam("date", "2023-10-01")
                .addHeader("Auth", "token")
                .download(new File("report.pdf"));
    }
}
