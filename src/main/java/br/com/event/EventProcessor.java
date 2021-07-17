package br.com.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;

@Slf4j
@RequiredArgsConstructor
public class EventProcessor {
    private final RabbitTemplate rabbitTemplate;
    private final String application;

    @EventListener(Event.class)
    public void processEvent(Event<?, ?> event) {
        String routingKey = event.getType().routingKey(event);
        log.info("Publisher new event: {} in routingKey: {}", event, routingKey);
        rabbitTemplate.convertAndSend(
                event.getType().topic(),
                routingKey,
                event.withApplication(application)
        );
    }
}
