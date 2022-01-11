package br.com.saga.executedStep;

import br.com.saga.orchestrator.ExecutedStepFailedMessage;
import br.com.saga.orchestrator.OrchestratorEvent;
import br.com.saga.executedEvent.ExecutedEvent;
import br.com.saga.step.Step;
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
                        ExecutedStepFailedMessage.builder()
                                .uuid(executedStep.getUuid())
                                .build(),
                        step.getFallbackRouter()
                )
        );
    }

    public ExecutedStep findExecutedStepStepAndExecutedEventByUuid(String uuid) {
        return executedStepRepository.findExecutedStepStepAndExecutedEventByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ExecutedStep not found: " + uuid));
    }

    public ExecutedStep findByUuid(String uuid) {
        return executedStepRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ExecutedStep not found"));
    }

    public ExecutedStep save(ExecutedStep executedStep) {
        return executedStepRepository.save(executedStep);
    }
}
