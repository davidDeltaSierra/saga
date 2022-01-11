package br.com.saga.event;

import br.com.saga.step.Step;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public Event findEventStepsById(Long id) {
        return eventRepository.findEventStepsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    public Optional<Step> nextStep(Long eventId, Integer position) {
        return eventRepository.findEventStepsByIdAndSteps_PositionGreaterThan(eventId, position)
                .stream()
                .flatMap(it -> it.getSteps().stream())
                .findFirst();
    }
}
