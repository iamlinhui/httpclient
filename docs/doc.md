```xml
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <version>2.2</version>
        <configuration>
        </configuration>
        <executions>
            <execution>
                <phase>package</phase>
                <goals>
                    <goal>shade</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
    <!--配置生成Javadoc包-->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-javadoc-plugin</artifactId>
        <version>2.10.4</version>
        <configuration>
            <encoding>UTF-8</encoding>
            <aggregate>true</aggregate>
            <charset>UTF-8</charset>
            <docencoding>UTF-8</docencoding>
        </configuration>
        <executions>
            <execution>
                <id>attach-javadocs</id>
                <goals>
                    <goal>jar</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</plugins>
```

* 在打成jar包时,使用ClassPathResource的getFile或者getPath之类的方式是不能获得结果的,但是可以使用classPathResource.getInputStream直接读取到
* class.getClassLoader().getResource()直接从resources目录下找,用的是相对路径,文件名（参数）前面不用加/
* class.getResource()是以resources为根目录的绝对路径,文件名（参数）前面需要加/
