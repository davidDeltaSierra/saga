package br.com.saga.mapper;

import br.com.saga.dto.response.ExecutedEventResponse;
import br.com.saga.model.ExecutedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExecutedEventMapper {
    ExecutedEventMapper INSTANCE = Mappers.getMapper(ExecutedEventMapper.class);

    ExecutedEventResponse executedEventToExecutedEventResponse(ExecutedEvent executedEvent);
}
