package br.com.saga.event;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

interface EventRepository extends PagingAndSortingRepository<Event, Long> {
    @EntityGraph(Event.Graph.eventSteps)
    Optional<Event> findEventStepsById(Long id);

    @EntityGraph(Event.Graph.eventSteps)
    Optional<Event> findEventStepsByIdAndSteps_PositionGreaterThan(Long id, Integer position);
}
