package readinglist;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class JpaRepositoriesBeanPostProcessor implements BeanPostProcessor {

        public JpaRepositoriesBeanPostProcessor(ConfigurableListableBeanFactory beanFactory){
            this.beanFactory = beanFactory;
        }
        private ConfigurableListableBeanFactory beanFactory;
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if(bean instanceof JpaRepositoriesAware){
                    ((JpaRepositoriesAware) bean).setRepositories(this.beanFactory.getBean(JpaRepositories.class.getName(), JpaRepositories.class));
                }

                return null;
            }
    }