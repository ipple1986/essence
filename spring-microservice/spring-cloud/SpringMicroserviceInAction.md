https://github.com/carnellj/spmia_overview

1.欢迎来到云，Spring
配置管理SpringCloudConfig(git/consul/Eureka)
服务发现SpringCloud sd(consul/Eureka)
日志/Http跟踪SpringCloudSleuth
异步消息SpringCloudStream
安全SpringCloudSecurity
弹性
1.Hystrix 断路/隔离机制 /client-loadbalance/failback/failover/failfast
2.Robbin loadbaland/+Eureka
构建/部署管道
TravisCI(http://travis-ci.org)
----------------------------------------------------------------------
Listing 1.1 Hello World with Spring Boot: a simple Spring microservice
GET http://localhost:8080/hello/john/carnell
HTTP STATUS:200
{"message": "Hello john carnell"}


package com.thoughtmechanix.simpleservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@SpringBootApplication
@RestController
@RequestMapping(value="hello")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	@RequestMapping(value="/{firstName}/{lastName}",method = RequestMethod.GET)
	public String hello( @PathVariable("firstName") String firstName,@PathVariable("lastName") String lastName) {
		return String.format("{\"message\":\"Hello %s %s\"}",firstName, lastName);
	}
}

-----------------------------------------------------------------------------
Listing 1.2 Hello World Service using Spring Cloud

package com.thoughtmechanix.simpleservice;
//Removed other imports for conciseness
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
@SpringBootApplication
@RestController
@RequestMapping(value="hello")
@EnableCircuitBreaker//启用Hystrix+Robbin库
@EnableEurekaClient //启用Eureka服务发现客户端
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@HystrixCommand(threadPoolKey = "helloThreadPool")//定义hystrix线程池，将请求方法委托给Hystrix管理的线程
	public String helloRemoteServiceCall(String firstName,String lastName){
		//Eureka服务发现
		ResponseEntity<String> restExchange =restTemplate.exchange( 
			"http://logical-service-id/name/[ca]{firstName}/{lastName}",
			HttpMethod.GET,
			null, String.class, firstName, lastName);

		return restExchange.getBody();
	}

	@RequestMapping(value="/{firstName}/{lastName}",method = RequestMethod.GET)
	public String hello( @PathVariable("firstName") String firstName,@PathVariable("lastName") String lastName) {
		return helloRemoteServiceCall(firstName, lastName) ；//间隔调helloRemoteServiceCall
	}
}
2.使用SpringBoot构建微服务
-------------------skeleton of microservice---------
--Listing 2.1 Maven pom file for the licensing service---pom.xml---
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
http://maven.apache.org/xsd/maven-4.0.0.xsd">
<modelVersion>4.0.0</modelVersion>
<groupId>com.thoughtmechanix</groupId>
<artifactId>licensing-service</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>jar</packaging>
<name>EagleEye Licensing Service</name>
<description>Licensing Service</description>
<parent>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-parent</artifactId>
	<version>1.4.4.RELEASE</version>
	<relativePath/>
</parent>
<dependencies>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
</dependencies>
<!—-Note: Some the build properties and Docker build plugins have been
excluded from the pom.xml in this pom (not in the source code in the
github repository) because they are not relevant to our discussion here.
-->
<build>
<plugins>
<plugin>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-maven-plugin</artifactId>
</plugin>
</plugins>
</build>
</project>

package com.thoughtmechanix.licenses;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);//返回 ApplicationContext
	}
}
------LicenseServiceController.java---
package com.thoughtmechanix.licenses.controllers;
import … // Removed for conciseness
@RestController
@RequestMapping(value="/v1/organizations/{organizationId}/licenses")
public class LicenseServiceController {
	//Body of the class removed for conciseness
}
---------LicenseServiceController.getLicenses方法--------------------
@RequestMapping(value="/{licenseId}",method = RequestMethod.GET)
public License getLicenses(@PathVariable("organizationId") String organizationId,@PathVariable("licenseId") String licenseId) {
	return new License().withId(licenseId).withProductName("Teleco").withLicenseType("Seat").withOrganizationId("TestOrg");
}
mvn spring-boot:run

服务打包 mvn clean package && java –jar target/licensing-service-0.0.1-SNAPSHOT.jar
配置管理 
服务发现 service启动时注册（别名+物理IP)
监控服务健康 actuator
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

3.配置管理
Listing 3.1 Setting up the pom.xml for the Spring Cloud configuration server

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
<modelVersion>4.0.0</modelVersion>
<groupId>com.thoughtmechanix</groupId>
<artifactId>configurationserver</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>jar</packaging>
<name>Config Server</name>
<description>Config Server demo project</description>
<parent>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-parent</artifactId>
	<version>1.4.4.RELEASE</version>
</parent>

<dependencyManagement>
<dependencies>
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-dependencies</artifactId>
	<version>Camden.SR5</version>
	<type>pom</type>
	<scope>import</scope>
</dependency>
</dependencies>
</dependencyManagement>

<properties>
	<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	<start-class>com.thoughtmechanix.confsvr.ConfigServerApplication </start-class>
	<java.version>1.8</java.version>
	<docker.image.name>johncarnell/tmx-confsvr</docker.image.name>
	<docker.image.tag>chapter3</docker.image.tag>
</properties>
<dependencies>
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-config-server</artifactId>
</dependency>
</dependencies>
<!--Docker build Config Not Displayed -->
</project>

------------------------
package com.thoughtmechanix.confsvr;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
@SpringBootApplication
@EnableConfigServer//
public class ConfigServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}
}