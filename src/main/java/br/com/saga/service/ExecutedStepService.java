package br.com.saga.service;

import br.com.saga.dto.response.ExecutedStepFailedResponse;
import br.com.saga.event.OrchestratorEvent;
import br.com.saga.model.ExecutedEvent;
import br.com.saga.model.ExecutedStep;
import br.com.saga.model.OperationStatus;
import br.com.saga.model.Step;
import br.com.saga.repository.ExecutedStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExecutedStepService {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ExecutedStepRepository executedStepRepository;

    public ExecutedStep factoryExecutedStep(Step step, ExecutedEvent executedEvent, String payload) {
        return ExecutedStep.builder()
                .step(step)
                .executedEvent(executedEvent)
                .payload(payload)
                .uuid(UUID.randomUUID().toString())
                .init(LocalDateTime.now())
                .status(OperationStatus.PROCESSING)
                .build();
    }

    public void publisherExecutedStepFailedResponse(ExecutedStep executedStep, Step step) {
        applicationEventPublisher.publishEvent(
                OrchestratorEvent.DYNAMIC_ROUTER.newInstance(
                        ExecutedStepFailedResponse.builder()
                                .uuid(executedStep.getUuid())
                                .build(),
                        step.getFallbackRouter()
                )
        );
    }

    public ExecutedStep findExecutedStepStepAndExecutedEventByUuid(String uuid) {
        return executedStepRepository.findExecutedStepStepAndExecutedEventByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ExecutedStep not found"));
    }

    public ExecutedStep findByUuid(String uuid) {
        return executedStepRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ExecutedStep not found"));
    }

    public ExecutedStep save(ExecutedStep executedStep) {
        return executedStepRepository.save(executedStep);
    }
}
