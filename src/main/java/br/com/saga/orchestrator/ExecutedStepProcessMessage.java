package br.com.saga.orchestrator;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ExecutedStepProcessMessage {
    String uuid;
    String jsonRaw;
    String successRouter;
    String fallbackRouter;
}
