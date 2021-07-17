package br.com.saga.config;

import br.com.saga.util.BeanFactoryUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static java.util.Objects.isNull;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.rabbitmq")
public class AppRabbitmqProperties {
    private static AppRabbitmqProperties SPRING_INSTANCE;

    private String topic;
    private String successQueue;
    private String fallbackQueue;

    public static AppRabbitmqProperties getInstance() {
        if (isNull(SPRING_INSTANCE)) {
            SPRING_INSTANCE = BeanFactoryUtil.getBean(AppRabbitmqProperties.class);
        }
        return SPRING_INSTANCE;
    }
}
