package readinglist;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;

@Configuration
public class MyConfiguration {
    @Bean
    public static BeanDefinitionRegistryPostProcessor beanFactoryPostProcessorxx(){
        return new JpaRepositoriesBeanFactoryPostProcessor() ;
    }
}
