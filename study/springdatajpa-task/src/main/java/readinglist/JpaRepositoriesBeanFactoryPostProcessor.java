package readinglist;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Arrays;

public class JpaRepositoriesBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        BeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(JpaRepositories.class).getBeanDefinition();
        beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(JpaRepositories.class.getName(),beanDefinition);

        ListableBeanFactory listableBeanFactory = (ListableBeanFactory)registry;
        ManagedMap<String, RuntimeBeanReference> managedMap = new ManagedMap<>();
        Arrays.stream(listableBeanFactory.getBeanNamesForType(ReaderRepository.class)).forEach(
                s->
                        managedMap.putIfAbsent(s,new RuntimeBeanReference(s))

        );
        Arrays.stream(listableBeanFactory.getBeanNamesForType(JpaRepositories.class)).forEach(
                s->
                        registry.getBeanDefinition(s).getConstructorArgumentValues().addGenericArgumentValue(managedMap)
        );

        beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(MySchedulingConfigurer.class).getBeanDefinition();
        beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        beanDefinition.getPropertyValues().addPropertyValue("repositories",new RuntimeBeanReference(JpaRepositories.class.getName()));
        registry.registerBeanDefinition(MySchedulingConfigurer.class.getName(),beanDefinition);


    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.ignoreDependencyInterface(JpaRepositoriesAware.class);
        beanFactory.addBeanPostProcessor(new JpaRepositoriesBeanPostProcessor(beanFactory));
    }
}
