package br.com.saga.test.util;

import br.com.saga.model.ExecutedEvent;
import br.com.saga.model.OperationStatus;

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
