package br.com.saga.test.mapper;

import br.com.saga.dto.response.ExecutedEventResponse;
import br.com.saga.mapper.ExecutedEventMapper;
import br.com.saga.test.util.MapperMockUtil;
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
