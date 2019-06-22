package spring.microservice.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceConfig{
    @Value("${dbname}")
    private String dbname;
    @Value("${spring.datasource.password}")
    private String springDatasourcePassword;

    public String getSpringDatasourcePassword() {
        return springDatasourcePassword;
    }

    public String getDbname() {
        return dbname;
    }
}