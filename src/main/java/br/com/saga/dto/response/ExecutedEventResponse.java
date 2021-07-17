package br.com.saga.dto.response;

import br.com.saga.model.OperationStatus;
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
