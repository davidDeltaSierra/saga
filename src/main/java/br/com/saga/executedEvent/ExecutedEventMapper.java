package br.com.saga.executedEvent;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExecutedEventMapper {
    ExecutedEventMapper INSTANCE = Mappers.getMapper(ExecutedEventMapper.class);

    ExecutedEventResponse executedEventToExecutedEventResponse(ExecutedEvent executedEvent);
}
