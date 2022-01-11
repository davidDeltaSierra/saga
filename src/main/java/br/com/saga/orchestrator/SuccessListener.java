package br.com.saga.orchestrator;

import br.com.event.Event;
import br.com.saga.event.EventService;
import br.com.saga.executedEvent.ExecutedEvent;
import br.com.saga.executedEvent.ExecutedEventService;
import br.com.saga.executedStep.ExecutedStep;
import br.com.saga.executedStep.ExecutedStepService;
import br.com.saga.executedStep.OperationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
class SuccessListener {
    private final ExecutedEventService executedEventService;
    private final ExecutedStepService executedStepService;
    private final TransactionTemplate transactionTemplate;
    private final EventService eventService;

    @RabbitListener(queues = "#{orchestratorSuccess.getName()}")
    void successListener(Event<ExecutedStepSuccessMessage, OrchestratorEvent> event) {
        ExecutedStep executedStep = executedStepService.findExecutedStepStepAndExecutedEventByUuid(event.getPayload().getUuid());
        if (executedStep.isFinalized()) {
            executedStepService.publisherExecutedStepFailedResponse(executedStep, executedStep.getStep());
            throw new RuntimeException("This ExecutedStep is already finalized with status: " + executedStep.getStatus());
        }
        Optional<ExecutedStep> newStep = factoryNewStep(
                executedStep.getExecutedEvent().getEventId(),
                executedStep.getStep().getPosition(),
                executedStep.getExecutedEvent(),
                event.getPayload().getJsonRaw()
        );
        transactionTemplate.execute((status) -> {
            finalizeExecutedStep(executedStep);
            newStep.ifPresentOrElse(
                    this::saveAndPublisherExecutedStepSuccessResponse,
                    () -> finalizeExecutedEvent(executedStep.getExecutedEvent())
            );
            return null;
        });
    }

    private void saveAndPublisherExecutedStepSuccessResponse(ExecutedStep executedStep) {
        executedStepService.save(executedStep);
        executedEventService.publisherExecutedStepSuccessResponse(executedStep, executedStep.getStep());
    }

    private void finalizeExecutedEvent(ExecutedEvent executedEvent) {
        executedEvent.setEnd(LocalDateTime.now());
        executedEvent.setStatus(OperationStatus.FINISHED);
        executedEventService.save(executedEvent);
    }

    private void finalizeExecutedStep(ExecutedStep executedStep) {
        executedStep.setEnd(LocalDateTime.now());
        executedStep.setStatus(OperationStatus.FINISHED);
        executedStepService.save(executedStep);
    }

    private Optional<ExecutedStep> factoryNewStep(Long eventId,
                                                  Integer position,
                                                  ExecutedEvent executedEvent,
                                                  String payload) {
        return eventService.nextStep(eventId, position)
                .map(step -> executedStepService.factoryExecutedStep(step, executedEvent, payload));
    }
}
