package br.com.event;

public interface EventFactory<T extends EventType> {
    <P> Event<P, T> newInstance(P payload);

    default <P> Event<P, T> newInstance(P payload, String router) {
        return this.newInstance(payload);
    }
}
