package br.com.saga.event;

import br.com.event.Event;
import br.com.event.EventFactory;
import br.com.event.EventType;
import br.com.saga.config.AppRabbitmqProperties;

public enum OrchestratorEvent implements EventType, EventFactory<OrchestratorEvent> {
    DYNAMIC_ROUTER {
        private String router;

        @Override
        public String topic() {
            return AppRabbitmqProperties
                    .getInstance()
                    .getTopic();
        }

        @Override
        public <P> Event<P, OrchestratorEvent> newInstance(P payload) {
            throw new RuntimeException("Use dynamic router");
        }

        @Override
        public <P> Event<P, OrchestratorEvent> newInstance(P payload, String router) {
            this.router = router;
            return Event.<P, OrchestratorEvent>builder()
                    .type(this)
                    .payload(payload)
                    .build();
        }

        @Override
        public String routingKey(Event<?, ?> event) {
            return router;
        }
    }
}
