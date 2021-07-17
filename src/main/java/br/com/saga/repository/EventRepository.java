package br.com.saga.repository;

import br.com.saga.model.Event;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface EventRepository extends PagingAndSortingRepository<Event, Long> {
    @EntityGraph(Event.Graph.eventSteps)
    Optional<Event> findEventStepsById(Long id);

    @EntityGraph(Event.Graph.eventSteps)
    Optional<Event> findEventStepsByIdAndSteps_PositionGreaterThan(Long id, Integer position);
}
