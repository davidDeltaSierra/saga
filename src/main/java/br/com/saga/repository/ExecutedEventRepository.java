package br.com.saga.repository;

import br.com.saga.model.ExecutedEvent;
import br.com.saga.model.OperationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ExecutedEventRepository extends PagingAndSortingRepository<ExecutedEvent, Long> {
    @EntityGraph(ExecutedEvent.Graph.executedEventEventAndExecutedSteps)
    Optional<ExecutedEvent> findExecutedEventEventAndExecutedStepsByUuid(String uuid);

    @EntityGraph(ExecutedEvent.Graph.executedEventEventAndExecutedStepsAndExecutedSteps_Step)
    Optional<ExecutedEvent> findExecutedEventEventAndExecutedStepsById(Long id);

    @EntityGraph(ExecutedEvent.Graph.executedEventEventAndExecutedSteps)
    List<ExecutedEvent> findAllExecutedEventEventAndExecutedStepsByStatus(OperationStatus status);

    @EntityGraph(ExecutedEvent.Graph.executedEventEventAndExecutedStepsAndExecutedSteps_Step)
    List<ExecutedEvent> findAllExecutedEventEventAndExecutedStepsAndExecutedStepsStepByEndIsNull();
}
