package br.com.saga.event;

import br.com.event.Event;
import br.com.event.EventFactory;
import br.com.event.EventType;
import br.com.saga.config.AppRabbitmqProps;

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
    }
}
