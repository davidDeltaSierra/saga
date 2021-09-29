package br.com.saga.dto.message;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ExecutedStepSuccessMessage {
    String uuid;
    String jsonRaw;
}
