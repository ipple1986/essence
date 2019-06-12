### 1.Bootstarting Spring 开启Spring
#### 1.1 Spring rebooted(Spring再生)
````text
* 自动配置
* 依赖管理
````
```text
* Spring2.5前，需要大量XML配置
* Spring2.5后，提供基于注解组件扫描方式
* Spring3.0后提供基于JAVA类配置文件
---Spring Boot 出现
```
##### 1.1.1 重新审视Spring
使用Spring创建一个hellword Web应用，你至少要做以下这些:
```text
* 项目结构（构建工具Gradle/Maven）+ 依赖组件（MVC,ServletAPI等依赖）
* 声明Spring的DispatcherServlet(web.xml或WebApplicationInitializer)
* Spring配置对MVC的支持（xml配置或注入MVC的@Bean）
* Conttroller对Http请求响应"Hellop Word"  //只有这一项是跟开发有关
* 需要一个应用服务器，部署应用，如Tomcat
```
HelloController.groovy
```groovy
@RestController//上面没有import语句
class HelloController{
    @RequestMapping("/")
    def hello(){
        return "Hello Work"
    }
}
```
使用Spring CLI运行
//HelloController.groovy未编译,CLI安装往下看
```bash
$ spring run HelloController.groovy
```
##### 1.1.2 掌握SpringBoot要点
```text
* 自动配置
* Starter依赖
* 命令行接口CLI //不需要传统项目构建
* Actuator //检查运行中应用的内部信息
```
* 自动配置

假设你正在开发一个访问数据库的应用，需要用到JdbcTemplate,基于JAVA配置类，你需要这么做
```text
@Bean
public JdbcTemplate jdbcTemplate(DataSource dataSource){
    return new JdbcTemplate(dataSource);
}
```
依赖了DataSource,你还得注入数据源Bean,以H2为例
````text
public DataSource datasource(){
    return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScripts("schema.sql","data.sql")
            .build();
}
````
虽然配置这两个类都不是很复杂很长，但是很多应用，都要重复写这样的样板配置。

如果数据库包在类路径下，将自动为你注入数据源DataSource类，

同样JdbcTempate在类路径下，也同样会实例一个JdbcTemplate实例，供其他类注入使用

* Starter依赖

如果你要开发一个RestAPI,使用SpringMVC,支持JSON展示，能够使用声明式验证JSR303规范，需要内嵌Tomcat;你需要添加以下依赖到maven/gradle
```text
org.springframework:spring-core
org.springframework:spring-web
org.springframework:spring-webmvc
com.fasterxml.jackson.core:jackson-databind
org.hibernate:hibernate-validator
org.apache.tomcat.embed:tomcat-embed-core
org.apache.tomcat.embed:tomcat-embed-el
org.apache.tomcat.embed:tomcat-embed-logging-juli
```
如果你使用Starter依赖，只需要依赖SpringBoot web 依赖
```text
 org.springframework.boot:spring-boot-starter-web
```
每一类starter有自己的作用，比如security/jpa等，你不再需要担心包依赖/版本冲突等问题，经过测试的一组包被放在同一个starter的某个版本上。

* CLI
```text
* 上面例子HelloControler.groovy,没有import语句，声明RequestMapping RestController.
* CLI发现那些使用类型（RequestMapping RestController）在哪些Starter依赖里
* 找到Starter依赖后，加入类路径，触发自动配置
* 自动配置后就注入了DispatcherServlet/MVC,最后Controller能响应http请求
```

* Actuator //检查运行中应用的内部信息

主要检查的内容：
```text
* SpringBoot上下文中配置了哪些Bean
* SpringBoot自动配置的决策是什么
* 各种变量:环境变量、系统变量、配置变量、命令行参数
* 追踪最近HTTP请求
* 线程当前状态
* 各种指标:内存使用情况，垃圾回归，web请求数，数据源使用情况等
```
Actuator暴露这些信息通过以下方式
```text
* Web端
* Shell接口//ssh进应用
```

##### 1.1.3 SpringBoot 不是什么

误解
```text
* SpringBoot并不是应用服务器，只是嵌套第三方服务器，Tomcat/jetty/undertow
* SpringBoot并不是实现任何企业JAVA规范，只是支持规范实现（JPA实现）进行自动配置
* SpringBoot只是利用Spring4的条件配置+ 构建工具（maven/gradle）的传递依赖来实现它的魔术
```
#### 1.2 SpringBoot入门

##### 1.2.1 安装 SpringBoot CLI
* 手动下载发布包 // http://repo.spring.io/release/org/springframework/boot/spring-boot-cli/
```text
* 下载发布包
* 解压到指定目录
* 将bin目录加入path变量//*unix下，可创建符号链接到/usr/bin下，方便升级
```
```text
$ spring --version
```
* SDKMAN安装 //可安装/管理多个版本SpringBoot CLI 版本
```text
$ curl -s get.sdkman.io | bash //然后执行 输出的命令 ，完成sdk的安装
$ source "/home/username/.sdkman/bin/sdkman-init.sh"
```
```text
$ sdk install springboot
$ spring --version
```
```text
$ sdk list springboot//显示当前已安装的所有版本，以及当前哪个版本被 使用
$ sdk install springboot 1.3.0.RELEASE //安装指定版本
$ sdk use springboot 1.3.0.RELEASE //切换版本
$ sdk default springboot 1.3.0.RELESE //设置默认版本
```
* MAC OSX下homebrew方式安装
```text
ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/
master/install)"
$ brew tap pivotal/tap
$ brew install springboot
$ spring --version
```

* MacPorts 方式
```text
$ sudo port install spring-boot-cli//MacPorts安装好
$ spring --version
```
* SpringBoot CLI 自动完成
```text
* MacPorts不支持
* Homebrew方式已经支持
* 手动/sdk //source或点 安装目录shell-completion下的bash或zsh脚本
* Windows下执行springboot shell
```

##### 1.2.2 使用pringBootInitializer初始化一个SpringBoot项目
初始化SpringBoot的方式
````text
* 官网 https://start.spring.io
* IDEA
* STS // Spring Tool Suite 3.4.0+ 
* SpringBoot CLI
````
```bash
$ spring help init //init 相当于Initializer的客户端
$ spring init -l/--list //查看init所有参数
```
```text
$ spring init [--dependencies|-d]web,jpa,security --build [gradle|maven] -p [war|jar] [-x|解压目录]  
```

### 2.开发第一个SpringBoot应用
使用CLI初始化器创建一个初始化的SpringBoot应用（readinglist）
```text
* 使用web，支持spring mvc
* 使用themylefa,做web视图
* 使用jpa，做持久化
* 使用h2,做数据源
```
```bash
$ spring init -dweb,thymeleaf,jpa,h2 --build gradle -p jar readinglist
```
springboot cli生成的项目默认是demo，包/构件名demo+DemoApplication，改成ReadingListXXX
#### 让SpringBoot工作起来
##### 2.1.1 探索SpringBoot项目的结构
```text
readinglist //项目顶文件夹
    build.gradle //构建脚本
    src/
        main/
            java/
                readinglist/
                    ReadingListApplication.java //SpringBoot启动/配置主类
            resources/ //配置文件
                application.properties //主配置文件
                static  //存放静态资源 文件
                templates //视图模板文件
        test
            java
                readinglist/
                    ReadingListApplicationTests.java //加载springboot上下文
            resources //测试配置文件
```
启动SpringBoot:ReadingListApplication.java 
```text
package readinglist;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication// Configuration + ComponentScan + EnableAutoConfiguration from springboot1.2.0 合成一个注解
public class ReadingListApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReadingListApplication.class, args);//以可执行jar方式启动
    }
}
```
Gradle构建与运行
```bash
$ grandle bootRun// Maven构建时，使用mvn spring-boot:run
```
上面等价于
```bash
$ gradle build
...
$ java -jar build/libs/readinglist-0.0.1-SNAPSHOT.jar
```
测试SpringBoot:ReadingListApplicationTest.java
```test
package readinglist;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import readinglist.ReadingListApplication;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ReadingListApplication.class)//取代传统ContextConfiguration注解
@WebAppConfiguration
public class ReadingListApplicationTests {
    @Test
    public void contextLoads() {
    }
}
``` 
配置文件:application.properties
```text
server.port=8000 //默认tomcat8080
```
##### 2.1.2 解剖SpringBoot项目构建
Gradle方式：build.gradle
```text
buildscript { //导入spring-boot插件
    ext {
        springBootVersion = `1.3.0.RELEASE`
    }
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
    }
}
apply plugin: 'java'
apply plugin: 'eclipse'
apply plugin: 'idea'
apply plugin: 'spring-boot' //使用springboot插件
jar {
    baseName = 'readinglist'
    version = '0.0.1-SNAPSHOT'
}
sourceCompatibility = 1.7
targetCompatibility = 1.7
repositories {
    mavenCentral()
}
dependencies {
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.springframework.boot:spring-boot-starter-data-jpa")
    compile("org.springframework.boot:spring-boot-starter-thymeleaf")
    runtime("com.h2database:h2")
    testCompile("org.springframework.boot:spring-boot-starter-test")
}
eclipse {
    classpath {
        containers.remove('org.eclipse.jdt.launching.JRE_CONTAINER')
        containers 'org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.7'
    }
}
task wrapper(type: Wrapper) {
    gradleVersion = '1.12'
}
```
Maven:方式
```text
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.manning</groupId>
    <artifactId>readinglist</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>
    <name>ReadingList</name>
    <description>Reading List Demo</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>{springBootVersion}</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <start-class>readinglist.ReadingListApplication</start-class>
        <java.version>1.7</java.version>
    </properties>
    <build>
        <plugins>
            <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```
#### 2.2使用Starter依赖管理
解决包依赖/版本问题，提供代表各种功能点的starter包，避免自己去找需要的各种包

#####  2.2.1 指定特殊功能的Starter依赖
```text
dependencies {
    compile "org.springframework.boot:spring-boot-starter-web"
    compile "org.springframework.boot:spring-boot-starter-thymeleaf"
    compile "org.springframework.boot:spring-boot-starter-data-jpa"
    compile "com.h2database:h2"
    testCompile("org.springframework.boot:spring-boot-starter-test")
}
```
```text
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
```
查看项目依赖
```bash
$ gradle dependencies
$ mvn dependency:tree
```
#####  2.2.2 覆盖Starter的传递 依赖
web Starter依赖了json包jackson，如果排除它
```text
//Gradle
compile("org.springframework.boot:spring-boot-starter-web") {
    exclude group: 'com.fasterxml.jackson.core'
}
//Maven
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>com.fasterxml.jackson.core</groupId>
        </exclusion>
    </exclusions>
</dependency>
```
替换成高版本(2.3.4->2.4.3),构建脚本直接添加以下依赖
```text
//Gradle ！！！！最新优先，如果替换旧版本，需要先排除，再添加
compile("com.fasterxml.jackson.core:jackson-databind:2.4.3")
//Maven 最近优先
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.4.3</version>
</dependency>

```
#### 2.3 使用自动配置
将近200多个类似以下的自动监测自动配置的场景
```text
* JdbcTemplate在类路径下？如果存在数据源，自动配置JdbcTemplate
* Thymeleaf在类路径下？自动配置模板/视图解析器，模板引擎
* SpringSecurity在类路径下？自动配置基于Web安全
```
##### 2.3.1 关注在应用功能上
定义Domain实体Book
```text
package readinglist;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String reader;
    private String isbn;
    private String title;
    private String author;
    private String description;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getReader() {
        return reader;
    }
    public void setReader(String reader) {
        this.reader = reader;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
```
定义Repository仓库接口
```text
package readinglist;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReadingListRepository extends JpaRepository<Book, Long> {//JpaRepository 18个通用接口
        List<Book> findByReader(String reader);
}
```
定义Web接口Controller
```text
package readinglist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.List;
@Controller
@RequestMapping("/")
public class ReadingListController {
    private ReadingListRepository readingListRepository;
    @Autowired
    public ReadingListController(ReadingListRepository readingListRepository) {
        this.readingListRepository = readingListRepository;
    }
    @RequestMapping(value="/{reader}", method=RequestMethod.GET)
    public String readersBooks(@PathVariable("reader") String reader,Model model) {
        List<Book> readingList = readingListRepository.findByReader(reader);   
        if (readingList != null) {
            model.addAttribute("books", readingList);
        }
        return "readingList";
}
    @RequestMapping(value="/{reader}", method=RequestMethod.POST)
    public String addToReadingList(@PathVariable("reader") String reader, Book book) {
        book.setReader(reader);
        readingListRepository.save(book);
        return "redirect:/{reader}";
    }
}
```
src/main/resorces/templates/readingList.html
```text
<html>
<head>
<title>Reading List</title>
<link rel="stylesheet" th:href="@{/style.css}"></link>
</head>
<body>
<h2>Your Reading List</h2>
<div th:unless="${#lists.isEmpty(books)}">
    <dl th:each="book : ${books}">
        <dt class="bookHeadline">
                <span th:text="${book.title}">Title</span> by<span th:text="${book.author}">Author</span>(ISBN: <span th:text="${book.isbn}">ISBN</span>)
        </dt>
        <dd class="bookDescription">
            <span th:if="${book.description}" th:text="${book.description}">Description</span>
            <span th:if="${book.description eq null}">No description available</span>
        </dd>
    </dl>
</div>
<div th:if="${#lists.isEmpty(books)}">
    <p>You have no books in your book list</p>
</div>
<hr/>
<h3>Add a book</h3>
<form method="POST">
    <label for="title">Title:</label><input type="text" name="title" size="50"></input><br/>
    <label for="author">Author:</label><input type="text" name="author" size="50"></input><br/>
    <label for="isbn">ISBN:</label><input type="text" name="isbn" size="15"></input><br/>
    <label for="description">Description:</label><br/>
    <textarea name="description" cols="80" rows="5"></textarea><br/>
    <input type="submit"></input>
</form>
</body>
</html>
```
src/main/resources/static/style.css
````text
body {
    background-color: #cccccc;
    font-family: arial,helvetica,sans-serif;
}
.bookHeadline {
    font-size: 12pt;
    font-weight: bold;
}
.bookDescription {
    font-size: 10pt;
}
label {
    font-weight: bold;
}
````
开发完成了，背后发生了什么？
##### 2.3.3 到底发生了什么？
依赖于Spring4.0带来的条件配置（spring-boot-configuration.jar包含一些配置类）
```text
package readinglist;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
public class JdbcTemplateCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context,AnnotatedTypeMetadata metadata) {
        try {
            context.getClassLoader().loadClass("org.springframework.jdbc.core.JdbcTemplate");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```
MyService被创建只有当JdbcTemplate在类路径下
```text
@Conditional(JdbcTemplateCondition.class)
    public MyService myService() {
        ...
    }
```
SpringBoot定义一系列 条件注解，并使用在它的配置类上
```text
@ConditionalOnBean          …指定Bean被配置
@ConditionalOnMissingBean   …指定Bean还没被配置
@ConditionalOnClass         …指定类在类路径中
@ConditionalOnMissingClass  …指定类不在类路径中
@ConditionalOnExpression    …(SpEL)表达式为true
@ConditionalOnJava          …匹配特定或某范围JDK版本
@ConditionalOnJndi          …JNDI初始化Context可用 并且存在JNDI位置
@ConditionalOnProperty      …某属性值被配置
@ConditionalOnResource      …类路径下存在某可用资源 
@ConditionalOnWebApplication …应用是Web应用
@ConditionalOnNotWebApplication …应用不是Web应用
```
DataSourceAutoConfiguration.java
```text
@Configuration
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })//当这些类在类路径中时，才注入这个配置
@EnableConfigurationProperties(DataSourceProperties.class) //使用application.properties哪些配置
@Import({ Registrar.class, DataSourcePoolMetadataProvidersConfiguration.class})//导入其他配置类
public class DataSourceAutoConfiguration {
    ...
    @Configuration
    @Conditional(DataSourceAutoConfiguration.DataSourceAvailableCondition.class)//只有当DataSource可用时，提供JdbcTemplate自动配置
    protected static class JdbcTemplateConfiguration {

        @Autowired(required = false)
        private DataSource dataSource;

        @Bean
        @ConditionalOnMissingBean(JdbcOperations.class)
        public JdbcTemplate jdbcTemplate() {
            return new JdbcTemplate(this.dataSource);
        }
     }
}
```
其他自动配置例子
```text
* h2在类路径上，自动创建嵌套的h2数据库
* Hibernate EntityManager存在类路径，自动创建LocalContainerEntityManagerFactoryBean/JpaVendorAdapter
* SpringDataJPA在类路径上，自动创建Repository接口的实现类
* Thymeleaf在类路径上，自动创建视图/模板解析器，模板引擎，模板解析路径/templates
* SpringMVC在类路径上，自动配置DispathcerServlet,启用SpringMVC
* Tomcat类在类路径上，自动启动内嵌Tomcat,默认端口8080
```

### 3. 自定义SpringBoot配置
```text
* 覆盖自动配置的类
* 自定义属性
* 自定义错误页
```
#### 3.1 覆盖自动配置的类
##### 3.1.1 使你的应用更安全
在readinglist应用中 添加security starter
```text
//Gradle
compile("org.springframework.boot:spring-boot-starter-security")
//Maven
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
再次访问时，浏览器弹出输入用户密码的输入框，用户名user/密码需要从启动日志中读取，这样很不友好，而且还要去日志找密码
##### 3.1.2 自定义安全配置
跳过SpringSecurity自动配置的安全认证，使用自定义的安全验证规则
```text
package readinglist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.
builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private ReaderRepository readerRepository;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                .antMatchers("/").access("hasRole('READER')") //需要读角色
                .antMatchers("/**").permitAll()//允许 所有人访问
            .and()
                .formLogin()  //设置登录路径
                    .loginPage("/login")
                    .failureUrl("/login?error=true");//带error属性表示出了什么错
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(new UserDetailsService() {
                @Override
                public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException {
                    return readerRepository.findOne(username);//基于数据库，内存，LDAP
                }
            });
    }
}
```
```text
package readinglist;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReaderRepository extends JpaRepository<Reader, String> {}
```
Reader实现安全框架的UserDetails类，覆盖方法
```text
package readinglist;
import java.util.Arrays;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
@Entity
public class Reader implements UserDetails {
    private static final long serialVersionUID = 1L;
    @Id
    private String username;
    private String fullname;
    private String password;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    // UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
           return Arrays.asList(new SimpleGrantedAuthority("READER"));//授权READER角色
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    //不lock,expire,disable
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
```
#### 3.1.3 从别一面看SpringBoot自动配置
JdbcTemplate实现了JdbcOperations接口，如果不存在JdbcOperations，才会创建JdbcTemplate
```text
@Bean
@ConditionalOnMissingBean(JdbcOperations.class)
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(this.dataSource);
    }
}
```
SpringBootWebSecurityConfiguration中使用ConditionalOnMissingBean注解，当WebSecurityConfiguration没被 配置时注入默认SpringSecurity
```text
@Configuration
@EnableConfigurationProperties
@ConditionalOnClass({ EnableWebSecurity.class })
@ConditionalOnMissingBean(WebSecurityConfiguration.class)
@ConditionalOnWebApplication
public class SpringBootWebSecurityConfiguration {
    ...
}
```
SpringBoot优先处理应用级别的配置类，再处理自动配置类
```text
@EnableWebSecurity//此处间接创建了WebSecurityConfiguration，取代了SpringBootWebSecurityConfiguration
public class SecurityConfig extends WebSecurityConfigurerAdapter { ... }
```
#### 3.2 自定义属性配置（细粒度配置，超过300多个项）
不显示启动图案条,命令行参数
```bash
$ java -jar  readinglist-0.0.1-SNAPSHOT.jar --spring.main.show-banner=false
```
application.properites
```text
spring.main.show-banner=false
```
application.yaml
```text
spring:
    main:
        show-banner: false
```
环境变量
```text
export spring_main_show_banner=false
```
SpringBoot应用通过以下方式获取变量，优化级从高到低（前面覆盖后面）
```text
* 命令行参数
* JNDI属性，从java:comp/env
* JVM system properties
* 以random.*开头生成的随机生成数
* application.properites/application.yml
* 注解了PropertiesSource
* 默认属性
```
application.properites/application.yml存放位置，查找优化级高到低
```text
* 当前springboot运行的config子目录 ./config
* 当前springboot运行目录 .
* 包名为config下 src/main/java/config
* 根据类路径下src/main/resources
```
注：application.properites/application.yml存放在同一位置时，applicaton.yml会覆盖application.properties中的配置

##### 3.2.1 调优--自动配置
关闭Thymeleaf缓存 
```text
$ java -jar readinglist-0.0.1-SNAPSHOT.jar --spring.thymeleaf.cache=false
```
```text
spring:
    thymeleaf:
        case:
            false
```
SpringBoot支持其他模板缓存 ，默认值都是true
```text
* spring.freemarker.cache
* spring.velocity.cache
* spring.groovy.template.cache
```
配置内置服务器
```text
server.port=8444
```
添加https
```text
$ keytool -keystore mykeys.jks -genkey -alias tomcat -keyalg RSA
```
```text
server:
    port: 8443
    ssl:
        key-store: file:///path/to/mykeys.jks
        key-store-password: letmein
        key-password: letmein
```
日志配置（默认logback ,info）
其他日志实现，先排除logback
```text
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
//Gradle
configurations {
    all*.exclude group:'org.springframework.boot',module:'spring-boot-starter-logging'
}
```
```text
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j</artifactId>
</dependency>
//Gradle
compile("org.springframework.boot:spring-boot-starter-log4j")
```
完全控制你的日志，使用logback.xml,(建议使用默认loback记录日志,logback.xml放于srm/main/resources下)
```text
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>
                %d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
            </pattern>
        </encoder>
    </appender>
    
    <logger name="root" level="INFO"/>
    
    <root level="INFO">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```
不用logback.xml配置日志级别
```text
logging:
    path: /var/logs/
    file: BookWorm.log
    level:
        root: WARN
    config:
        classpath:logback.xml
    org.springframework.security: DEBUG
```
数据源配置
```text
spring:
    datasource:
        url: jdbc:mysql://localhost/readinglist
        username: dbuser
        password: dbpass
        driver-class-name: com.mysql.jdbc.Driver
```
配置了jndi-name,将覆盖其他数据源配置（因优先级高）
```text
spring:
    datasource:
        jndi-name: java:/comp/env/jdbc/readingListDS
```
##### 3.2.2 外部配置
我们在展示书列表时，带上亚马逊链接，同时需要亚马逊关联ID，以便亚马逊知道是从我们网上链过去，以得到一些报酬
```text
<a th:href="'http://www.amazon.com/gp/product/'
+ ${book.isbn}
+ '/tag=' + ${amazonID}"
th:text="${book.title}">Title</a>
```
上面amazonID，要从Controller中得到，EnableConfigurationProperties（支持自动配置类，已经加了这个注解）后，才能用ConfigurationProperties
```text
@Controller
@RequestMapping("/")
@ConfigurationProperties(prefix="amazon")//定义配置前缀
public class ReadingListController {
    private String associateId;
    
    private ReadingListRepository readingListRepository;
    @Autowired
    public ReadingListController(ReadingListRepository readingListRepository) {
        this.readingListRepository = readingListRepository;
    }
    public void setAssociateId(String associateId) {//通过set方法，从application.properties中注入
        this.associateId = associateId;
    }
    @RequestMapping(method=RequestMethod.GET)
    public String readersBooks(Reader reader, Model model) {
        List<Book> readingList =    readingListRepository.findByReader(reader);
        if (readingList != null) {
            model.addAttribute("books", readingList);
            model.addAttribute("reader", reader);
            model.addAttribute("amazonID", associateId);//将亚马逊ID传给前端
        }
        return "readingList";
    }
    @RequestMapping(method=RequestMethod.POST)
    public String addToReadingList(Reader reader, Book book) {
        book.setReader(reader);
        readingListRepository.save(book);
        return "redirect:/";
    }
}
```
application.properties
```text
amazon.associateId=habuma-20 //amazon.associate_id ,amazon.associate-id 都支持
```

##### 3.2.3 配置profile,配置归类 
```text
@Profile("production")
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    ...
}
```
```bash
$ java -jar readinglist-0.0.1-SNAPSHOT.jar --spring.profiles.active=production
```
```text
spring.profiles.active=production
```
使用properteis格式的多profile应用，每个环境配置一个application-{env}.properties,共用的放在application.properties

application.properties
```text
amazon.associateId=habuma-20
logging.level.root=INFO
```
application-developement.properties
```text
logging.level.root=DEBUG
```
application-production.properties
```text
logging.path=/var/logs/
logging.file=BookWorm.log
logging.level.root=WARN
```
使用yaml格式的多profile，可以只使用一个application.yml//使用---分隔，用spring.profiles配置profile
```text
amazon.associateId: habuma-20
logging.level.root: INFO
--- 
spring.profiles: developement
logging.level.root: DEBUG
---
spring.profiles: production
logging.path: /var/logs/
logging.file: BookWorm.log
logging.level.root: WARN
```
#### 3.3 自定义错误页面
/main/resources/templates/error.html
```text
<html>
<head>
<title>Oops!</title>
<link rel="stylesheet" th:href="@{/style.css}"></link>
</head>
<body>
<div class="errorPage">
<span class="oops">Oops!</span><br/>
<img th:src="@{/MissingPage.png}"></img>
<p>There seems to be a problem with the page you requested
(<span th:text="${path}"></span>).</p>
<p th:text="${'Details: ' + message}"></p>
</div>
</body>
</html>
```
error对象有以下属性
```text
* timestamp     —错误发生时间
status          —HTTP状态码
error           —出错原因
exception       —异常类名
message         —异常信息
errors          — 从BindingResult异常产生的任何报错
trace           —异常栈追踪
path            —URL请求路径
```

### 4. 测试SpringBoot应用
#### 4.1 自动配置集成测试
传统Spring集成测试例子
```text
@RunWith(SpringJUnit4ClassRunner.class)//启用集成测试
@ContextConfiguration(classes=AddressBookConfiguration.class)//加载Spring上下文
public class AddressServiceTests {
    @Autowired
    private AddressService addressService;
    @Test
    public void testService() {
        Address address = addressService.findByLastName("Sheman");
        assertEquals("P", address.getFirstName());
        assertEquals("Sherman", address.getLastName());
        assertEquals("42 Wallaby Way", address.getAddressLine1());
        assertEquals("Sydney", address.getCity());
        assertEquals("New South Wales", address.getState());
        assertEquals("2000", address.getPostCode());
    }
}
```
SpringBoot集成测试

（SpringApplicationConfiguration/SpringBootServletInitializer：除加载Spring上下文，还读到配置、显示日志和其他SpringBoot特性功能）
```text
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=AddressBookConfiguration.class)
public class AddressServiceTests {
    ...
}
```
#### 4.2 测试web应用,使用SpringMockMVC //模拟一个servlet 服务器
##### 4.2.1 SpringMock MVC  模拟MockMVC
```text
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ReadingListApplication.class)
@WebAppConfiguration // 让SpringJUnit4ClassRunner生成WebApplicationContext，而不是ApplicationContext类
public class MockMvcWebTests {
    @Autowired
    private WebApplicationContext webContext; //此处注入
    private MockMvc mockMvc;
    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webContext).build();
    }
    @Test
    public void homePage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/readingList"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("readingList"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("books"))
        .andExpect(MockMvcResultMatchers.model().attribute("books",Matchers.is(Matchers.empty())));
    }
    @Test
    public void postBook() throws Exception {
        mockMvc.perform(post("/readingList")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("title", "BOOK TITLE")
            .param("author", "BOOK AUTHOR")
            .param("isbn", "1234567890")
            .param("description", "DESCRIPTION"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location", "/readingList"));
    
            Book expectedBook = new Book();
            expectedBook.setId(1L);
            expectedBook.setReader("craig");
            expectedBook.setTitle("BOOK TITLE");
            expectedBook.setAuthor("BOOK AUTHOR");
            expectedBook.setIsbn("1234567890");
            expectedBook.setDescription("DESCRIPTION");
            
            mockMvc.perform(get("/readingList"))
            .andExpect(status().isOk())
            .andExpect(view().name("readingList"))
            .andExpect(model().attributeExists("books"))
            .andExpect(model().attribute("books", hasSize(1)))
            .andExpect(model().attribute("books",contains(samePropertyValuesAs(expectedBook))));
    }
}
```

##### 4.2.2 测试web安全性

依赖
```text
testCompile("org.springframework.security:spring-security-test")
//Maven
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```
配置MockMVC
```text
@Before
public void setupMockMvc() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webContext)
            .apply(springSecurity())//加入SpringSecurity
            .build();
}
```
测试未认证
```text
@Test
public void homePage_unauthenticatedUser() throws Exception {
    mockMvc.perform(get("/")).andExpect(status().is3xxRedirection())
        .andExpect(header().string("Location","http://localhost/login"));
}
```
使用WithMockUser
```text
@Test
@WithMockUser(username="craig",password="password",roles="READER")
public void homePage_authenticatedUser() throws Exception {
    ...
}
```
使用WithUserDetails
```text
@Test
@WithUserDetails("craig")//声明craig,当执行些方法时，会被 加入安全上下文中
public void homePage_authenticatedUser() throws Exception {
    Reader expectedReader = new Reader();
    expectedReader.setUsername("craig");
    expectedReader.setPassword("password");
    expectedReader.setFullname("Craig Walls");
    
    mockMvc.perform(get("/"))
    .andExpect(status().isOk())
    .andExpect(view().name("readingList"))
    .andExpect(model().attribute("reader",samePropertyValuesAs(expectedReader)))
    .andExpect(model().attribute("books", hasSize(0)))
}
```
#### 4.2 web集成测试
```text
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=ReadingListApplication.class)
@WebIntegrationTest
public class SimpleWebTest {
    @Test(expected=HttpClientErrorException.class)
    public void pageNotFound() {
        try {
            RestTemplate rest = new RestTemplate();
            rest.getForObject("http://localhost:8080/bogusPage", String.class);
            fail("Should result in HTTP 404");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            throw e;
        }
    }
}
```
#### 4.2.1 使用随机端口
```text
@WebIntegrationTest(value="server.port=0")
或
@WebIntegrationTest(randomPort=true)
```
```text
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=ReadingListApplication.class)
@WebIntegrationTest
public class SimpleWebTest {
    @Value("${local.server.port}")//注入随机端口
    private int port;
    
    @Test(expected=HttpClientErrorException.class)
    public void pageNotFound() {
        try {
            RestTemplate rest = new RestTemplate();
            rest.getForObject("http://localhost:{port}/bogusPage", String.class);//使用port变量
            fail("Should result in HTTP 404");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            throw e;
            
        }
    }
}
```
#### 4.2.2 使用selenium测试html页面
html页面的测试，你最多只能测试返回全部内容，不能细测到部分内容，以及无法模拟点击等操作。

Selenium启动一个浏览器，并在浏览器上下文中测试用例

添加Selenium依赖
```text
testCompile "org.seleniumhq.selenium:selenium-java:2.45.0"
```
测试用例
```text
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTests(classes=ReadingListApplication.class)
@WebIntegrationTest(randomPort=true)
public class ServerWebTests {
    private static FirefoxDriver browser;
    @Value("${local.server.port}")
    private int port;

    @BeforeClass
    public static void openBrowser() {
        browser = new FirefoxDriver();//可用其他浏览器
        browser.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }
    @AfterClass
    public static void closeBrowser() {
        browser.quit();
    }
    @Test
    public void addBookToEmptyList() {
        
        String baseUrl = "http://localhost:" + port;
        browser.get(baseUrl);
        assertEquals("You have no books in your book list",browser.findElementByTagName("div").getText());
        browser.findElementByName("title").sendKeys("BOOK TITLE");
        browser.findElementByName("author").sendKeys("BOOK AUTHOR");
        browser.findElementByName("isbn").sendKeys("1234567890");
        browser.findElementByName("description").sendKeys("DESCRIPTION");
        browser.findElementByTagName("form").submit();
        
        WebElement dl = browser.findElementByCssSelector("dt.bookHeadline");
        assertEquals("BOOK TITLE by BOOK AUTHOR (ISBN: 1234567890)",dl.getText());
        WebElement dt = browser.findElementByCssSelector("dd.bookDescription");
        assertEquals("DESCRIPTION", dt.getText());
    }
}

```
### 5.Groovy + SpringBootCLI 
* Java语言有代码噪音：import导入语法、类/方法修饰符(private/public)、语句尾分号、属性setter/getter
* 使用CLI：不需要构建脚本，Grab机制取代构建工具，执行代码类导入/类库依赖，最后可产生部署包
#### 5.1 开发一个Groovy SpringBoot项目
创建项目根目录readinglist,子目录static/templates,将style.css/readingList.html拷贝过来
```bash
$ mkdir -p readinglist/static
$ mkdir -p readinglist/templates
```
创建实体Book.groovy + schema.sql
```text
class Book {
    Long id
    String reader
    String isbn
    String title
    String author
    String description
}
```
```text
create table Book (
id identity,
reader varchar(20) not null,
isbn varchar(10) not null,
title varchar(50) not null,
author varchar(50) not null,
description varchar(2000) not null
);
```
自定义Repository数据持久层，使用JDBCTemplate取代JPA（JPA需要编译成.class，总之CLI与JPA不兼容）

ReadingListRepository.groovy接口定义
```text
interface ReadingListRepository {
    List<Book> findByReader(String reader)
    void save(Book book)
}

```
JdbcReadingListRepository.groovy JDBC Repository实现
```text
@Repository
class JdbcReadingListRepository implements ReadingListRepository {
    @Autowired
    JdbcTemplate jdbc

    List<Book> findByReader(String reader) {
        jdbc.query(
                "select id, reader, isbn, title, author, description " +
                        "from Book where reader=?",
                { rs, row ->
                    new Book(id: rs.getLong(1),
                            reader: rs.getString(2),
                            isbn: rs.getString(3),
                            title: rs.getString(4),
                            author: rs.getString(5),
                            description: rs.getString(6))
                } as RowMapper,
                reader)
    }
    void save(Book book) {
        jdbc.update("insert into Book " +
                "(reader, isbn, title, author, description) " +
                "values (?, ?, ?, ?, ?)",
                book.reader,
                book.isbn,
                book.title,
                book.author,
                book.description)
    }
}
```
定义Controller
```text
@Controller
@RequestMapping("/")
class ReadingListController {
    String reader = "Craig"
    @Autowired
    ReadingListRepository readingListRepository
    @RequestMapping(method=RequestMethod.GET)
    def readersBooks(Model model) {
        List<Book> readingList =
                readingListRepository.findByReader(reader)
        if (readingList) {
            model.addAttribute("books", readingList)
        }
        "readingList"
    }
    @RequestMapping(method=RequestMethod.POST)
    def addToReadingList(Book book) {
        book.setReader(reader)
        readingListRepository.save(book)
        "redirect:/"
    }
}
```
定义缺少依赖入口，Grabs.groovy类
```text
//Groovy代码里没有关于h2/thymeleaf类的引用，cli无法识别兼容失败，无法触发自动依赖传递管理
//告诉CLI运行时，抓取h2/thymeleaf依赖库
@Grab("h2")
@Grab("spring-boot-starter-thymeleaf")
class Grabs{}
```
运行,浏览器访问http://localhost:8080
```bash
$ spring run .
```

#### 5.1 Grab抓取依赖
SpringMVC/JDBC等（RequestMapping,JdbcTemplate）Groovy编译失败，会触发CLI抓取必要的依赖库

但编译成功，没有添加必要的库，如H2,Thymeleaf库包，使用Groovy的Grab高级打包引擎

```text
@Grab(group="com.h2database", module="h2", version="1.4.190"
@Grab("com.h2database:h2:1.4.185")//CLI根据自己的版本，查找对应H2版本
@Grab("h2") //CLI有一批自已知道的完整依赖，先查moduleId,找不到再找groupId:moduleId,再找不到就完整查找
```
传递依赖
```text
@GrabMetadata(“com.myorg:custom-versions:1.0.0”)
```
在MavenRepo/com/myorg/有个custom-versions.properties,每行格式：groupId:moduleId=version
```text
com.h2database:h2=1.4.186
```
使用Spring IO Platform
```text
@GrabMetadata('io.spring.platform:platform-versions:1.0.4.RELEASE')
```
设置依赖仓库地址
```text
@GrabResolver(name='jboss', root=
'https://repository.jboss.org/nexus/content/groups/public-jboss')
```
#### 5.3测试Groovy版本SpringBoot
```bash
$ mkdir tests
```
```text
import org.springframework.test.web.servlet.MockMvc
import static.org.springframework.test.web.servlet.setup.MockMvcBuilders.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.mockito.Mockito.*
class ReadingListControllerTest {
    @Test
    void shouldReturnReadingListFromRepository() {
        List<Book> expectedList = new ArrayList<Book>()
        expectedList.add(new Book(
                id: 1,
                reader: "Craig",
                isbn: "9781617292545",
                title: "Spring Boot in Action",
                author: "Craig Walls",
                description: "Spring Boot in Action is ..."
        ))
        def mockRepo = mock(ReadingListRepository.class)
        when(mockRepo.findByReader("Craig")).thenReturn(expectedList)

        def controller = new ReadingListController(readingListRepository: mockRepo)

        MockMvc mvc = standaloneSetup(controller).build()
        mvc.perform(get("/"))
                .andExpect(view().name("readingList"))
                .andExpect(model().attribute("books", expectedList))
    }
}
```
执行测试
```text
$ spring test tests/ReadingListControllerTest.groovy
or
$ spring test tests
```
更好可读性的 Spock规范测试
```text
import org.springframework.test.web.servlet.MockMvc
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.mockito.Mockito.*

class ReadingListControllerSpec extends Specification {
    MockMvc mockMvc
    List<Book> expectedList
    def setup() {
        expectedList = new ArrayList<Book>()
        expectedList.add(new Book(
                id: 1,
                reader: "Craig",
                isbn: "9781617292545",
                title: "Spring Boot in Action",
                author: "Craig Walls",
                description: "Spring Boot in Action is ..."
        ))
        def mockRepo = mock(ReadingListRepository.class)
        when(mockRepo.findByReader("Craig")).thenReturn(expectedList)
        def controller =
                new ReadingListController(readingListRepository: mockRepo)
        mockMvc = standaloneSetup(controller).build()
    }
    def "Should put list returned from repository into model"() {//可读性更好
        when:
            def response = mockMvc.perform(get("/"))
        then:
            response.andExpect(view().name("readingList"))
                    .andExpect(model().attribute("books", expectedList))
    }
}
```
#### 5.3Groovy版本SpringBoot 打包
```bash
$ spring jar ReadingList.jar .
$ java -jar ReadingList.jar
```

### 6.使用Grails
#### 6.1 使用GORM作数据持久化
GORM定义实体Book.groovy
```test
package readinglist
import grails.persistence.*
@Entity
class Book {
    Reader reader
    String isbn
    String title
    String author
    String description
}
```
添加Grails Hibernate依赖
```text
<dependency>
<groupId>org.grails</groupId>
<artifactId>gorm-hibernate4-spring-boot</artifactId>
<version>1.1.0.RELEASE</version>
</dependency>
//OR
compile("org.grails:gorm-hibernate4-spring-boot:1.1.0.RELEASE")
```
可选：添加Grails Mongodb依赖
```text
<dependency>
<groupId>org.grails</groupId>
<artifactId>gorm-mongodb-spring-boot</artifactId>
<version>1.1.0.RELEASE</version>
</dependency>
//OR
compile("org.grails:gorm-mongodb-spring-boot:1.1.0.RELEASE")
```
定义Reader.groovy实体
```text
package readinglist
import grails.persistence.*
import org.springframework.security.core.GrantedAuthority
import
org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
@Entity
class Reader implements UserDetails {
    String username
    String fullname
    String password
    Collection<? extends GrantedAuthority> getAuthorities() {
        Arrays.asList(new SimpleGrantedAuthority("READER"))
    }
    boolean isAccountNonExpired() {
        true
    }
    boolean isAccountNonLocked() {
        true
    }
    boolean isCredentialsNonExpired() {
        true
    }
    boolean isEnabled() {
        true
    }
}
```
ReadingListController.groovy，实体直接操作数据器，无有Repository存在
````text
package readinglist
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
@Controller
@RequestMapping("/")
@ConfigurationProperties("amazon")
class ReadingListController {
    @Autowired
    AmazonProperties amazonProperties
    
    @ExceptionHandler(value=RuntimeException.class)
    @ResponseStatus(value=HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
    
    def error() {
        "error"
    }
    @RequestMapping(method=RequestMethod.GET)
    def readersBooks(Reader reader, Model model) {
        List<Book> readingList = Book.findAllByReader(reader) //findAllByReader GORM帮我们生成的方法
        model.addAttribute("reader", reader)
        if (readingList) {
            model.addAttribute("books", readingList)
            model.addAttribute("amazonID", amazonProperties.getAssociateId())
        }
        "readingList"
    }
    @RequestMapping(method=RequestMethod.POST)
    def addToReadingList(Reader reader, Book book) {
        Book.withTransaction { //GORM此处不同
            book.setReader(reader)
            book.save() //自己操作，保存自我
        }
        "redirect:/"
}
}
}
````
SORM版本的SecurityConfig.groovy
```text
package readinglist
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService

@Configuration
class SecurityConfig extends WebSecurityConfigurerAdapter {
    void configure(HttpSecurity http) throws Exception {
        http
        .authorizeRequests()
        .antMatchers("/").access("hasRole('READER')")
        .antMatchers("/**").permitAll()
        .and()
        .formLogin()
        .loginPage("/login")
        .failureUrl("/login?error=true")
    }
    void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
        .userDetailsService(
        { username -> Reader.findByUsername(username) } //此处GORM直接用Reader查数据库
         as UserDetailsService
        )
    }
}
```
#### 6.2 使用GSP模板
GSP HTML (g:if/g:else/${})
```text
compile("org.grails:grails-gsp-spring-boot:1.0.0")
```
readList.gsp //like jsp
```text
<!DOCTYPE html>
<html>
<head>
    <title>Reading List</title>
    <link rel="stylesheet" href="/style.css"></link>
</head>
<body>
    <h2>Your Reading List</h2>
    <g:if test="${books}">
        <g:each in="${books}" var="book">
            <dl>
                    <dt class="bookHeadline">
                    ${book.title} by ${book.author}
                    (ISBN: ${book.isbn}")
                    </dt>
                    <dd class="bookDescription">
                        <g:if test="book.description">
                        ${book.description}
                        </g:if>
                        <g:else>
                        No description available
                        </g:else>
                    </dd>
            </dl>
        </g:each>
    </g:if>
    <g:else>
         <p>You have no books in your book list</p>
    </g:else>
<hr/>
     <h3>Add a book</h3>
     <form method="POST">
         <label for="title">Title:</label><input type="text" name="title" value="${book?.title}"/><br/>
         <label for="author">Author:</label><input type="text" name="author" value="${book?.author}"/><br/>
         <label for="isbn">ISBN:</label><input type="text" name="isbn"  value="${book?.isbn}"/><br/>
         <label for="description">Description:</label><br/>
         <textarea name="description" rows="5" cols="80"> ${book?.description}</textarea>
         <input type="hidden" name="${_csrf.parameterName}"  value="${_csrf.token}" />
         <input type="submit" value="Add Book" />
     </form>
</body>
</html>
```
#### 6.3 混合使用SpringBoot+ Grails3
##### 6.3.1 创建Grails项目
```bash
$ grails create-app readinglist
```
生成的构建build.grale脚本
```text
apply plugin: "spring-boot"

dependencies {
    compile 'org.springframework.boot:spring-boot-starter-logging'
    compile("org.springframework.boot:spring-boot-starter-actuator")
    compile "org.springframework.boot:spring-boot-autoconfigure"
    compile "org.springframework.boot:spring-boot-starter-tomcat"
    ...
}
```
运行或打包运行
```bash
$ grails run-app
$ gradle bootRun
$ gradle build
$ jar -jar ./build/lib/readinglist-0.0.1.SNAPSHOT.jar
```
##### 6.3.2 定义Domain实体
生成Book.groovy/BookSpec.groovy两个类
```bash
$ grails create-domain-class Book
```
grails-app/domain/readinglist/Book.groovy内容如下
```text
package readinglist
class Book {
    static constraints = {
    }
    //以下为添加的自定义字段
    String reader
    String isbn
    String title
    String author
    String description
}
```
写Grails Controller
```text
$ grails create-controller //创建空白
$ grails generate-controller //创建带CRUD
$ grails genterate-all //创建带CRUE +关联给定Domain类型的视图
```
```bash
$ grails create-controller ReadingList //在grails-app/controllers下生成ReadingListController
```
```text
package readinglist
class ReadingListController {
    //def index() { }//默认处理/readinglist请求，转发到grails-app/views/readinglist/index.gsp
    def index() {
        respond Book.list(params), model:[book: new Book()]
    }
    @Transactional
    def save(Book book) {
        book.reader = 'Craig'
        book.save flush:true
        redirect(action: "index")
    }
}
```
创建Grails GSP视图文件index.gsp
```text
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/> <!--Grails的main布局 -->
    <title>Reading List</title>
    <link rel="stylesheet" href="/assets/main.css?compile=false" /><!--Grails项目中的样式 -->
    <link rel="stylesheet" href="/assets/mobile.css?compile=false" />
    <link rel="stylesheet" href="/assets/application.css?compile=false" />
</head>
<body>
    <h2>Your Reading List</h2>
        <g:if test="${bookList && !bookList.isEmpty()}">
            <g:each in="${bookList}" var="book">
                <dl>
                    <dt class="bookHeadline">
                        ${book.title}</span> by ${book.author} (ISBN: ${book.isbn}")
                    </dt>
                    <dd class="bookDescription">
                        <g:if test="${book.description}">
                                ${book.description}
                        </g:if>
                        <g:else>
                                No description available
                        </g:else>
                    </dd>
                </dl>
            </g:each>
        </g:if>
        <g:else>
            <p>You have no books in your book list</p>
        </g:else>
    <hr/>
        <h3>Add a book</h3>
        <g:form action="save">
            <fieldset class="form">
                <label for="title">Title:</label>
                    <g:field type="text" name="title" value="${book?.title}"/><br/>
                <label for="author">Author:</label>
                    <g:field type="text" name="author"  value="${book?.author}"/><br/>
                <label for="isbn">ISBN:</label>
                    <g:field type="text" name="isbn" value="${book?.isbn}"/><br/>
                <label for="description">Description:</label><br/>
                <g:textArea name="description" value="${book?.description}"  rows="5" cols="80"/>
            </fieldset>
            <fieldset class="buttons">
                <g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
            </fieldset>
        </g:form>
</body>
</html>
```
### 7.深入理解Actuator
#### 7.1 了解Actuator的端点EndPoint
* GET /autoconfig 提代自动配置报告描述（哪些自动配置成功，哪些失败）.
* GET /configprops 描述Bean使用了哪些配置属性被注入(包含默认值 ).
* GET /beans 描述所有Bean，以及之间关系
* GET /dump 查询当前线程活动的快照dump.
* GET /env 查询所有环境属性/变量
* GET /env/{name} 查询指定环境变量
* GET /health 报靠应用的健康指标，通过 HealthIndicator 实现.
* GET /info 查询应用自定义的信息（application.properties中，以info 前缀的）
* GET /mappings 描述所有URI路径和映射到哪些Controller (包含Actuator 端点endpoints).
* GET /metrics 报告各种应用指标，内存使用情况，Http请求数
* GET /metrics/{name} 查询具体指标
* POST /shutdown 关闭应用;只有 endpoints.shutdown.enabled 配置为true时有效 .
* GET /trace 提供Http基本跟踪信息(时间戳,头部信息等)
添加Actuator依赖
```text
//Gradle
compile 'org.springframework.boot:spring-boot-starter-actuator'
//Maven
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
//CLI
@Grab('spring-boot-starter-actuator')
```

看Bean的装载详情
```text
$ curl http://localhost/beans
```
主要包含五大信息
```text
* bean //beanid
* resource //.class存放位置
* dependencies //依赖beanId
* scope //是否单例
* type //java类型
```
自动配置情况
```text
$ curl http://localhost/autoconfig
```
显示哪些配置成功，哪些失败
```text
* positiveMatches 满足自动配置
* negativeMatches 不满足自动配置
```

#### 7.2 Shell远程连接 Actuator
添加依赖
```text
//GRADLE
compile("org.springframework.boot:spring-boot-starter-remote-shell")
//MAVEN
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-remote-shell</artifactId>
</dependency>
//CLI
@Grab("spring-boot-starter-remote-shell")
```
```bash
$ ssh user@localhost -p 2000
```
remote-shell提供以下命令
```text
* autoconf
* beans
* metrics
* endpoint //调用其他actuator支持的api,如health  $ endpoint invoke health
```
#### 7.3 用JXM监视应用
打开jconsole连接运行中的SpringBoot
在org.springframework.bootEndPoint.requestMappingEndPoint.Data有数据，可界面关闭应用
#### 7.4 自定义Actuator
##### 7.4.1修改endpoint的Id
```text
endpoints.shutdown.id=kill // /shutdown-> /kill
```
##### 7.4.2启用/禁用endpoint
```text
endpoints.shutdown.enabled=false
只启动shutdown
endpoints.enable=false
endpoints.shutdown.enable=true
```
##### 7.4.3 自定义指标
```text
package org.springframework.boot.actuate.metrics;
public interface CounterService {
    void increment(String metricName);
    void decrement(String metricName);
    void reset(String metricName);
}
package org.springframework.boot.actuate.metrics;
public interface GaugeService {
    void submit(String metricName, double value);
}
//自动注入接口
@Controller
@RequestMapping("/")
@ConfigurationProperties("amazon")
public class ReadingListController {
    ...
    private CounterService counterService;
    @Autowired
    public ReadingListController( ReadingListRepository readingListRepository,AmazonProperties amazonProperties,
    CounterService counterService, GaugeService gaugeService) {//注入接口
        this.readingListRepository = readingListRepository;
        this.amazonProperties = amazonProperties;
        this.counterService = counterService;
        this.gaugeService = gaugeService;
    }
    ...
    @RequestMapping(method=RequestMethod.POST)
    public String addToReadingList(Reader reader, Book book) {
        book.setReader(reader);
        readingListRepository.save(book);
        counterService.increment("books.saved"); //更新统计指标
        gaugeService.submit("books.last.saved", System.currentTimeMillis());//更新指标
        return "redirect:/";
    }
}
```
自定义多指标
```text
package org.springframework.boot.actuate.endpoint;
public interface PublicMetrics {
    Collection<Metric<?>> metrics();
}
```
实现ApplicationContextMetrics 多指标
```text
package readinglist;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
@Component
public class ApplicationContextMetrics implements PublicMetrics {
    private ApplicationContext context;
    @Autowired
    public ApplicationContextMetrics(ApplicationContext context) {
        this.context = context;
    }
    @Override
    public Collection<Metric<?>> metrics() {
        List<Metric<?>> metrics = new ArrayList<Metric<?>>();
        metrics.add(new Metric<Long>("spring.context.startup-date",context.getStartupDate()));
        metrics.add(new Metric<Integer>("spring.beans.definitions",context.getBeanDefinitionCount()));
        metrics.add(new Metric<Integer>("spring.beans",context.getBeanNamesForType(Object.class).length));
        metrics.add(new Metric<Integer>("spring.controllers",context.getBeanNamesForAnnotation(Controller.class).length));
        return metrics;
    }
}
```

##### 7.4.4 自定义跟踪信息存储
方式一：扩容
```text
package readinglist;
import org.springframework.boot.actuate.trace.InMemoryTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ActuatorConfig {
    @Bean
    public InMemoryTraceRepository traceRepository() {
        InMemoryTraceRepository traceRepo = new InMemoryTraceRepository();
        traceRepo.setCapacity(1000);//将默认100个实体改成1000个
        return traceRepo;
    }
}
```
方式二：改变存储形式，改为mongo
```text
package readinglist;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.Trace;
import org.springframework.boot.actuate.trace.TraceRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;
@Service
public class MongoTraceRepository implements TraceRepository {
    private MongoOperations mongoOps;
    @Autowired
    public MongoTraceRepository(MongoOperations mongoOps) {
        this.mongoOps = mongoOps;
    }
    @Override
    public List<Trace> findAll() {
        return mongoOps.findAll(Trace.class);
    }
    @Override
    public void add(Map<String, Object> traceInfo) {
        mongoOps.save(new Trace(new Date(), traceInfo));
    }
}
```
加入mongo starter依赖
````text
compile("org.springframework.boot:spring-boot-starter-data-mongodb")
//or
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
````

##### 7.4.5 自定义健康指标器 HealthIndicator
判断亚马逊是否可访问
```text
package readinglist;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
@Component
public class AmazonHealth implements HealthIndicator {
    @Override
    public Health health() {
        try {
            RestTemplate rest = new RestTemplate();
            rest.getForObject("http://www.amazon.com", String.class);
            return Health.up().build();
        } catch (Exception e) {
            //return Health.down().build();
            return Health.down().withDetail("reason", e.getMessage())//将错误显示到reason字段
        }
    }
}
```
SecurityConfig.java里边添加shutdown的角色权限
```text
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
    .authorizeRequests()
    .antMatchers("/").access("hasRole('READER')")
    .antMatchers("/shutdown","/metrics"...).access("hasRole('ADMIN')")//只有ADMIN角色才能ShutDown
    .antMatchers("/**").permitAll()
    .and()
    .formLogin()
    .loginPage("/login")
    .failureUrl("/login?error=true");
}
```
添加内存管理用用户
```text
@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(new UserDetailsService() {
                @Override
                public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException {
                    return readerRepository.findById(username).get();//基于数据库，内存，LDAP
                }
            })
                    .and()
                    .inMemoryAuthentication()
                    .withUser("admin").password("s3cr3t")//添加内存管理员账号
                    .roles("ADMIN", "READER");
    }
```
你不可能将所有endpoint点都关闭掉，使用配置管理上下文路径配置
```text
management.context-path=mgnt
```
修改WebSecurity.java
```text
.antMatchers("/mgnt/shutdown").access("hasRole('ADMIN')")
```
### 8.部署SpringBoot应用
#### 8.1 构建War文件
构建脚本修改
```text
apply plugin: 'war'
war {
    baseName = "readinglist"
    version = "0.0.1-SNAPSHOT"
}
//Maven
<packaging>war</packaging>
```
创建War文件支持SpringMVC的DispathcerServlet

SpringBootServletInitializer实现了WebApplicationInitializer,提供对Filter,Servlet,ServletContextInitializer的查找
```text
package readinglist;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
public class ReadingListServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ReadingListApplication.class);
    }
}
```
```text
package readinglist;
@SpringBootApplication
public class ReadingListApplication{
    	public static void main(String[] args) {
    		SpringApplication.run(ReadingListApplication.class, args);
    	}
}
```
构建
```text
$ gradle build //or mvn package
```
运行
```text
部署到tomcat 访问http://localhost/readinglist-0.0.1.SNAPSHOT
$ java -jar readinglist-0.0.1.SNAPSHOT.war
```

#### 8.2.2 创建生产级别 数据源Profile
方式一：替换springboot自动配置的数据源
````text
@Bean
@Profile("production") //生产时替换自动配置的数据源
public DataSource dataSource() {
    DataSource ds = new DataSource();
    ds.setDriverClassName("org.postgresql.Driver");
    ds.setUrl("jdbc:postgresql://localhost:5432/readinglist");
    ds.setUsername("habuma");
    ds.setPassword("password");
    return ds;
}
````
方式二：通过spring.datasource配置，spring.datasource有以下子属性：
```text
name 数据源名称
initialize 是否初始化data.sql脚本(默认true)
schema ddl脚本名称
data dml脚本名称
sql-script-encoding 读SQL文件的编码
platform 读schema ddl文件的方言
continue-on-error 初始化失败是否继续 （默认false）
separator SQL分隔符 (默认 ;)
driver-class-name JDBC驱动完整名称(通常能从URL自动获取)
url 数据库URL
username 数据库账号
password 数据库密码
jndi-name 查找数据源的JNDI名称
max-active 最大激活连接数 (默认100)
max-idle 最大空闲数 (默认 8)
min-idle 最小空闲数 (默认 8)
initial-size 初始化连接池大小 (默认10)
validation-query 验证数据库是否可以连接上的SQL
test-on-borrow 判断连接是否从连接池借来的 (默认 false )
test-on-return 判断连接是否来自连接池 (默认 false )
test-while-idle 空闲时，是否判断数据库连接(默认false )
time-between-eviction-runs-millis 多久执行淘汰连接的逻辑(默认5000ms )
min-evictable-idle-time-millis 连接空闲多久时间后被淘汰(默认 60000ms )
max-wait 当无可用连接时，连接池隔多久报错(默认30000ms )
jmx-enabled 数据源是否被 jmx管理(默认false)
```
```text
...
---
spring:
    profiles: production
    datasource:
        url: jdbc:postgresql://localhost:5432/readinglist
        username: habuma
        password: password
    jpa:
        database-platform: org.hibernate.dialect.PostgreSQLDialect
        hibernate.ddl-auto: create-drop //生产时，只能设置成update
```
#### 8.2.3 使 数据库 可移植化
```text
spring.jpa.hibernate.ddl-auto: update
另外一种方式，改写你的schema.sql只能被执行一次，二次执行失败
```  
理想：使用数据库移植库
```text
* flyway 使用SQL来定义移植脚本，每个脚本分配一个版本号，按顺序执行每个脚本并标记已执行，让数据库以达到期望的状态
* liquibase
```

Flyway方式集成

添加依赖
```text
//Gradle
compile("org.flywaydb:flyway-core")
//Maven
<dependency>
    <groupId>org.flywayfb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```
修改auto-ddl为none
```text
spring.jpa.hibernate.ddl-auto=none
```
db/migration/下定义SQL脚本，命名规则V[\d]+__描述文本.sql,两个下划线

/db/migration/V1__initialize.sql
```text
create table Reader (
    id serial primary key,
    username varchar(25) unique not null,
    password varchar(25) not null,
    fullname varchar(50) not null
);
create table Book (
    id serial primary key,
    author varchar(50) not null,
    description varchar(1000) not null,
    isbn varchar(10) not null,
    title varchar(250) not null,
    reader_username varchar(25) not null,
    foreign key (reader_username) references Reader(username)
);
create sequence hibernate_sequence;
insert into Reader (username, password, fullname)values ('craig', 'password', 'Craig Walls');
```
/db/migration/V2__insert_data_2_reader.sql
```text
后续脚本....
```

Liquibase方式

添加依赖
```text
//Gradle
compile("org.liquibase:liquibase-core")
//Maven
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
```
/db/changelog/db.changelog-master.yaml,解耦所有数据库平台的脚本
```text
databaseChangeLog:
    - changeSet:
        id: 1 //changeId
        author: habuma
        changes:
            - createTable:
                tableName: reader
                columns:
                    - column:
                        name: username
                        type: varchar(25)
                        constraints:
                            unique: true
                            nullable: false
                    - column:
                        name: password
                        type: varchar(25)
                        constraints:
                            nullable: false
                    - column:
                        name: fullname
                        type: varchar(50)
                        constraints:
                            nullable: false
            - createTable:
                tableName: book
                columns:
                    - column:
                        name: id
                        type: bigserial
                        autoIncrement: true
                        constraints:
                            primaryKey: true
                            nullable: false
                    - column:
                        name: author
                        type: varchar(50)
                        constraints:
                            nullable: false
                    - column:
                        name: description
                        type: varchar(1000)
                        constraints:
                            nullable: false
                    - column:
                        name: isbn
                        type: varchar(10)
                        constraints:
                            nullable: false
                    - column:
                        name: title
                        type: varchar(250)
                        constraints:
                            nullable: false
                    - column:
                        name: reader_username
                        type: varchar(25)
                        constraints:
                            nullable: false
                            references: reader(username)
                            foreignKeyName: fk_reader_username
            - createSequence: //创建hibernate序列
                sequenceName: hibernate_sequence
            - insert: //插入数据
                tableName: reader
                columns:
                    - column:
                        name: username
                        value: craig
                    - column:
                        name: password
                        value: password
                    - column:
                        name: fullname
                        value: Craig Walls
```
对比Flyway
```text
* Flyway每次更新，新添文件
* Liquibase只用一个文件，每次更新在changeset上添加一个递增的changeId,changeId可以是字符串只要保证唯一
```
Liquibase除yml格式，还支持xml,json格式
```text
liquibase.change-log = classpath:/db/changelog/db.changelog-master.xml
```
#### 8.3 部署SpringBoot到Pass云平台
```text
* CloudFoundry
* Heroku
```
##### 8.3.1 部署到 Cloud Foundry
```text
* 官网注册账号http://run.pivotal.io.
* 下载安装客户端cf
* 登录认证 cf login -a https://api.run.pivotal.io，输入邮箱/密码
// sbia-readinglist作为访问的二级域名，http://sbia-readinglist.cfapps.io
// 添加random-route,在sbia-readinglist后加追加随机字符，以确保全局唯一性
// 如： http://sbia-readinglist-gastroenterological-stethoscope.cfapps.io
* push 应用包  cf push sbia-readinglist -p build/libs/readinglist.war [--random-route]
* 重启应用 cf restart
* 查找CloudFoundry支持的数据库套餐  cf marketplace -s elephantsql//elephantsql 是postgresql的别名
* 创建数据库服务readinglistdb， cf create-service elephantsql turtle readinglistdb
* 绑定服务，只是环境变量VCAP_SERVICE的值   cf bind-service sbia-readinglist readinglistdb
* 重部署/重读取VCAP_SERVICE cf restage sbia-readinglist 
```
通过/health endpoit查看 db.database,确保应用使用了何种数据库
```text
{
    "status": "UP",
    "diskSpace": {
    "status": "UP",
    "free": 834331525120,
    "threshold": 10485760
},
"db": {
    "status": "UP",
    "database": "PostgreSQL",
    "hello": 1
}
```
##### 8.3.1 部署到 Heroku
heroku为你的应用提供一个git仓库，每次你推送代码时，为你构建部署应用
```bash
$ git init
// sbia-readinglist作为二级域名，访问http://sbia-readinglist.herokuapps.com,当然也可以随机
$ heroku apps:create sbia-readinglist //创建远程git仓库  https://git.heroku.com/sbia-readinglist.git
//maven方式构建时  web: java -Dserver.port=$PORT -jar target/readinglist.war ，在target目录下
// PORT是heroku在启动内置tomcat时，引用的端口中变量
$ echo "web: java -Dserver.port=$PORT -jar build/libs/readinglist.war" > Procfile //创建Procfile文件，告诉heroku以何种方式运行我们部署的应用
//heroku在构建脚本时，会触发task stage的任务，需要在build.gradle添加
task stage(dependsOn: ['build']) {}
//告诉heroku以哪个java版本构建我们应用,在项目根目录创建system.properties，加上以下内容
java.runtime.version=1.8
//推送代码，heroku读取Procfile，部署我们的应用，访问http://sbia-readinglist.herokuapps.com
$ git commit -am "Initial commit"
$ git push heroku master
//如果发现使用h2,想用postgresql，添加hobby-dev免费postgresql套餐
$ heroku addons:add heroku-postgresql:hobby-dev
//添加后发现还不可用
方式一：打开网页数据库管理界面，修改spring.datasource.*配置
$ heroku addons:open waking-carefully-3728
方式二：使用云连接starter
compile("org.springframework.boot:spring-boot-starter-cloud-connectors")
或
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-cloud-connectors</artifactId>
</dependency>
//设置profile=cloud
$ heroku config:set SPRING_PROFILES_ACTIVE="cloud"
//重新提交新添加的依赖，通过/health再查看就可以了
$ git commit -am "Add cloud connector"
$ git push heroku master
```