package br.com.saga.service;

import br.com.saga.config.AppRabbitmqProperties;
import br.com.saga.dto.request.ExecutedEventRequest;
import br.com.saga.dto.response.ExecutedStepSuccessResponse;
import br.com.saga.event.OrchestratorEvent;
import br.com.saga.model.*;
import br.com.saga.repository.ExecutedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExecutedEventService {
    private final AppRabbitmqProperties appRabbitmqProperties;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final TransactionTemplate transactionTemplate;
    private final ExecutedEventRepository executedEventRepository;
    private final ExecutedStepService executedStepService;
    private final EventService eventService;

    public ExecutedEvent saveAndInitOrchestration(ExecutedEventRequest executedEventRequest) {
        Event event = eventService.findEventStepsById(executedEventRequest.getEventId());
        Step step = event.getSteps()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event not contains steps"));
        ExecutedEvent executedEvent = factoryInitExecutedEvent(event);
        ExecutedStep executedStep = executedStepService.factoryExecutedStep(step, executedEvent, executedEventRequest.getPayload());
        publisherExecutedStepSuccessResponse(executedStep, executedStep.getStep());
        return transactionTemplate.execute((status) -> {
            ExecutedEvent ee = this.save(executedEvent);
            executedStepService.save(executedStep);
            return ee;
        });
    }

    public List<ExecutedEvent> findAllExecutedEventEventAndExecutedStepsByStatus(OperationStatus status) {
        return executedEventRepository.findAllExecutedEventEventAndExecutedStepsByStatus(status);
    }

    public ExecutedEvent findExecutedEventEventAndExecutedStepsByUuid(String uuid) {
        return executedEventRepository.findExecutedEventEventAndExecutedStepsByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    public ExecutedEvent findExecutedEventEventAndExecutedStepsById(Long id) {
        return executedEventRepository.findExecutedEventEventAndExecutedStepsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    public List<ExecutedEvent> findAllExecutedEventEventAndExecutedStepsAndExecutedStepsStepByEndIsNull() {
        return executedEventRepository.findAllExecutedEventEventAndExecutedStepsAndExecutedStepsStepByEndIsNull();
    }

    public ExecutedEvent save(ExecutedEvent executedEvent) {
        return executedEventRepository.save(executedEvent);
    }

    public void publisherExecutedStepSuccessResponse(ExecutedStep executedStep, Step step) {
        applicationEventPublisher.publishEvent(
                OrchestratorEvent.DYNAMIC_ROUTER.newInstance(
                        ExecutedStepSuccessResponse.builder()
                                .uuid(executedStep.getUuid())
                                .payload(executedStep.getPayload())
                                .successRouter(appRabbitmqProperties.getSuccessQueue())
                                .fallbackRouter(appRabbitmqProperties.getFallbackQueue())
                                .build(),
                        step.getProcessRouter()
                )
        );
    }

    public void failedExecutedEvent(ExecutedEvent executedEvent,
                                    ExecutedStep executedStepFailed,
                                    List<ExecutedStep> executedSteps,
                                    OperationStatus operationStatus) {
        executedEvent.setStatus(operationStatus);
        executedEvent.setEnd(LocalDateTime.now());
        executedStepFailed.setStatus(operationStatus);
        executedStepFailed.setEnd(LocalDateTime.now());
        transactionTemplate.execute((status) -> {
            this.save(executedEvent);
            executedStepService.save(executedStepFailed);
            executedSteps.forEach(it -> executedStepService.publisherExecutedStepFailedResponse(it, it.getStep()));
            return null;
        });
    }

    private ExecutedEvent factoryInitExecutedEvent(Event event) {
        return ExecutedEvent.builder()
                .event(event)
                .uuid(UUID.randomUUID().toString())
                .init(LocalDateTime.now())
                .status(OperationStatus.PROCESSING)
                .build();
    }
}
