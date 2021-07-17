package br.com.saga.repository;

import br.com.saga.model.ExecutedStep;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ExecutedStepRepository extends PagingAndSortingRepository<ExecutedStep, Long> {
    @EntityGraph(ExecutedStep.Graph.executedStepStepAndExecutedEvent)
    Optional<ExecutedStep> findExecutedStepStepAndExecutedEventByUuid(String uuid);

    Optional<ExecutedStep> findByUuid(String uuid);
}
