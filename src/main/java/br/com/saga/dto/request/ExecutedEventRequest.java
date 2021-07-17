package br.com.saga.dto.request;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Value
@Builder
@Jacksonized
public class ExecutedEventRequest {
    @NotNull
    Long eventId;
    @NotEmpty
    String payload;
}
