package br.com.saga.orchestrator;

import br.com.saga.common.config.AppRabbitmqProps;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
class BrokerConfig {
    private final AppRabbitmqProps appRabbitmqProps;

    @Bean
    Exchange orchestratorExchange() {
        return ExchangeBuilder
                .topicExchange(appRabbitmqProps.getTopic())
                .durable(true)
                .build();
    }

    @Bean
    Queue orchestratorSuccess() {
        return QueueBuilder
                .durable(appRabbitmqProps.getSuccessQueue())
                .build();
    }

    @Bean
    Binding orchestratorSuccessRouter(Exchange orchestratorExchange, Queue orchestratorSuccess) {
        return BindingBuilder
                .bind(orchestratorSuccess)
                .to(orchestratorExchange)
                .with(appRabbitmqProps.getSuccessQueue())
                .noargs();
    }

    @Bean
    Queue orchestratorFallback() {
        return QueueBuilder
                .durable(appRabbitmqProps.getFallbackQueue())
                .build();
    }

    @Bean
    Binding orchestratorFallbackRouter(Exchange orchestratorExchange, Queue orchestratorFallback) {
        return BindingBuilder
                .bind(orchestratorFallback)
                .to(orchestratorExchange)
                .with(appRabbitmqProps.getFallbackQueue())
                .noargs();
    }
}
