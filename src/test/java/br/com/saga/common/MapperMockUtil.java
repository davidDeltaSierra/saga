package br.com.saga.common;

import br.com.saga.executedEvent.ExecutedEvent;
import br.com.saga.executedStep.OperationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public final class MapperMockUtil {

    public static ExecutedEvent factoryExecutedEvent() {
        return ExecutedEvent.builder()
                .uuid(UUID.randomUUID().toString())
                .init(LocalDateTime.now())
                .end(LocalDateTime.now())
                .status(OperationStatus.PROCESSING)
                .eventId(1L)
                .build();
    }
}
