package br.com.saga.dto.response;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ExecutedStepSuccessResponse {
    String uuid;
    String payload;
    String successRouter;
    String fallbackRouter;
}
