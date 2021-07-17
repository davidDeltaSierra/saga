package br.com.saga.config;

import br.com.event.EventConfig;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig extends EventConfig {

    @Getter
    @Value("${spring.application.name:null}")
    private String ApplicationName;
}
