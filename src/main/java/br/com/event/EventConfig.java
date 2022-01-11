package br.com.event;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public abstract class EventConfig {
    public abstract String getApplicationName();

    @Bean
    @ConditionalOnMissingBean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter(final ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    RabbitTemplate amqpTemplate(final ConnectionFactory connectionFactory,
                                final Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
        return rabbitTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(final SimpleRabbitListenerContainerFactoryConfigurer configurer,
                                                                        final ConnectionFactory connectionFactory,
                                                                        final Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setMessageConverter(jackson2JsonMessageConverter);
        factory.setDefaultRequeueRejected(false);
        factory.setPrefetchCount(Runtime.getRuntime().availableProcessors());
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    EventProcessor eventProcessor(RabbitTemplate amqpTemplate) {
        return new EventProcessor(amqpTemplate, getApplicationName());
    }
}
