package br.com.saga.event;

import br.com.saga.config.AppRabbitmqProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BrokerConfig {
    private final AppRabbitmqProperties appRabbitmqProperties;

    @Bean
    public Exchange orchestratorExchange() {
        return ExchangeBuilder
                .topicExchange(appRabbitmqProperties.getTopic())
                .durable(true)
                .build();
    }

    @Bean
    public Queue orchestratorSuccess() {
        return QueueBuilder
                .durable(appRabbitmqProperties.getSuccessQueue())
                .build();
    }

    @Bean
    public Binding orchestratorSuccessRouter(Exchange orchestratorExchange, Queue orchestratorSuccess) {
        return BindingBuilder
                .bind(orchestratorSuccess)
                .to(orchestratorExchange)
                .with(appRabbitmqProperties.getSuccessQueue())
                .noargs();
    }

    @Bean
    public Queue orchestratorFallback() {
        return QueueBuilder
                .durable(appRabbitmqProperties.getFallbackQueue())
                .build();
    }

    @Bean
    public Binding orchestratorFallbackRouter(Exchange orchestratorExchange, Queue orchestratorFallback) {
        return BindingBuilder
                .bind(orchestratorFallback)
                .to(orchestratorExchange)
                .with(appRabbitmqProperties.getFallbackQueue())
                .noargs();
    }
}
