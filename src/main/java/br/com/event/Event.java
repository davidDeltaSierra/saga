package br.com.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;
import java.util.UUID;

@With
@Value
@Builder(toBuilder = true)
public class Event<P, T extends EventType> {
    @Builder.Default
    String uuid = UUID.randomUUID().toString();
    @Builder.Default
    LocalDateTime date = LocalDateTime.now();
    String application;
    String routing;
    P payload;
    T type;

    @JsonCreator
    public static <P, T extends EventType> Event<P, T> jacksonFactory(@JsonProperty("uuid") String uuid,
                                                                      @JsonProperty("date") LocalDateTime date,
                                                                      @JsonProperty("application") String application,
                                                                      @JsonProperty("routing") String routing,
                                                                      @JsonProperty("payload") P payload,
                                                                      @JsonProperty("type") T type) {
        return new Event<>(uuid, date, application, routing, payload, type);
    }
}
