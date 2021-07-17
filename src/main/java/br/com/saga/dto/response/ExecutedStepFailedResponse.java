package br.com.saga.dto.response;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ExecutedStepFailedResponse {
    String uuid;
}
