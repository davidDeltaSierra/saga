package br.com.saga.executedEvent;

import br.com.saga.common.MapperMockUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class ExecutedEventMapperTest {

    @Test
    void executedEventToExecutedEventResponseTest() {
        ExecutedEventResponse response = ExecutedEventMapper
                .INSTANCE.executedEventToExecutedEventResponse(MapperMockUtil.factoryExecutedEvent());
        Assertions.assertThat(response).hasNoNullFieldsOrPropertiesExcept();
    }
}
