package br.com.saga.dto.request;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ExecutedStepBrokerRequest {
    String uuid;
    String payload;
}
