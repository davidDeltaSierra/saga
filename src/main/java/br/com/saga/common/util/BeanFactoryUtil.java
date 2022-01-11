package br.com.saga.common.util;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class BeanFactoryUtil {
    private static BeanFactory beanFactory;

    @EventListener(ApplicationStartedEvent.class)
    public void init(ApplicationStartedEvent event) {
        BeanFactoryUtil.beanFactory = event.getApplicationContext();
    }

    public static <T> T getBean(Class<T> clazz) {
        return beanFactory.getBean(clazz);
    }

    public static <T> ObjectProvider<T> getBeanProvider(Class<T> clazz) {
        return beanFactory.getBeanProvider(clazz);
    }
}
