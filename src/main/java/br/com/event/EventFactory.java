package br.com.event;

public interface EventFactory<T extends EventType> {
    <P> Event<P, T> newInstance(P payload);

    default <P> Event<P, T> newInstance(P payload, String router) {
        throw new RuntimeException("By default dynamic router is not implemented");
    }
}
