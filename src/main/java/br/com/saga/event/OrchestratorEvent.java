package br.com.saga.event;

import br.com.event.Event;
import br.com.event.EventFactory;
import br.com.event.EventType;
import br.com.saga.config.AppRabbitmqProps;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum OrchestratorEvent implements EventType, EventFactory<OrchestratorEvent> {
    DYNAMIC_ROUTER {
        @Override
        public String topic() {
            return AppRabbitmqProps
                    .getInstance()
                    .getTopic();
        }

        @Override
        public <P> Event<P, OrchestratorEvent> newInstance(P payload) {
            throw new RuntimeException("Use dynamic router");
        }

        @Override
        public <P> Event<P, OrchestratorEvent> newInstance(P payload, String router) {
            return Event.<P, OrchestratorEvent>builder()
                    .type(this)
                    .payload(payload)
                    .routing(router)
                    .build();
        }

        @Override
        public String routingKey(Event<?, ?> event) {
            return event.getRouting();
        }
    };

    @JsonCreator
    public static OrchestratorEvent jacksonFactory(String value) {
        return Arrays.stream(OrchestratorEvent.values())
                .filter(it -> it.toString().equalsIgnoreCase(value))
                .findFirst()
                .orElse(DYNAMIC_ROUTER);
    }
}
