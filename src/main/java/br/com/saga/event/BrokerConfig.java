package br.com.saga.event;

import br.com.saga.config.AppRabbitmqProps;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BrokerConfig {
    private final AppRabbitmqProps appRabbitmqProps;

    @Bean
    public Exchange orchestratorExchange() {
        return ExchangeBuilder
                .topicExchange(appRabbitmqProps.getTopic())
                .durable(true)
                .build();
    }

    @Bean
    public Queue orchestratorSuccess() {
        return QueueBuilder
                .durable(appRabbitmqProps.getSuccessQueue())
                .build();
    }

    @Bean
    public Binding orchestratorSuccessRouter(Exchange orchestratorExchange, Queue orchestratorSuccess) {
        return BindingBuilder
                .bind(orchestratorSuccess)
                .to(orchestratorExchange)
                .with(appRabbitmqProps.getSuccessQueue())
                .noargs();
    }

    @Bean
    public Queue orchestratorFallback() {
        return QueueBuilder
                .durable(appRabbitmqProps.getFallbackQueue())
                .build();
    }

    @Bean
    public Binding orchestratorFallbackRouter(Exchange orchestratorExchange, Queue orchestratorFallback) {
        return BindingBuilder
                .bind(orchestratorFallback)
                .to(orchestratorExchange)
                .with(appRabbitmqProps.getFallbackQueue())
                .noargs();
    }
}
