package spring.microservice.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@RefreshScope
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);//返回 ApplicationContext
    }
}
/*
mvn spring-boot:run
mvn clean package && java –jar target/user-service-0.0.1-SNAPSHOT.jar
默认只暴露两个endpoint[info/health],通过配置# management.endpoints.web.exposure.include=*设置
默认endpoint上下文/actuator,通过management.endpoints.web.base-path=xxx设置
下载jdk8 jce
https://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
*/
