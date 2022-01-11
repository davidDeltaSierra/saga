package br.com.saga.executedEvent;

import br.com.saga.executedStep.OperationStatus;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ExecutedEventResponse {
    String uuid;
    OperationStatus status;
}
