package br.com.event;

public interface EventType {
    String topic();

    default String routingKey(Event<?, ?> event) {
        return (payloadRouter(event.getPayload()) + "." + eventTypeRouter()).toLowerCase();
    }

    default String payloadRouter(Object payload) {
        return payload.getClass().getSimpleName();
    }

    default String eventTypeRouter() {
        return !this.getClass().getSimpleName().isBlank()
                ? this.getClass().getSimpleName()
                : this.toString();
    }
}
