package br.com.saga.event;

import br.com.event.Event;
import br.com.saga.dto.request.ExecutedStepBrokerRequest;
import br.com.saga.model.ExecutedEvent;
import br.com.saga.model.ExecutedStep;
import br.com.saga.model.OperationStatus;
import br.com.saga.service.EventService;
import br.com.saga.service.ExecutedEventService;
import br.com.saga.service.ExecutedStepService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SuccessListener {
    private final ExecutedEventService executedEventService;
    private final ExecutedStepService executedStepService;
    private final TransactionTemplate transactionTemplate;
    private final EventService eventService;

    @RabbitListener(queues = "#{orchestratorSuccess.getName()}")
    void successListener(Event<ExecutedStepBrokerRequest, ?> event) {
        ExecutedStep executedStep = executedStepService.findExecutedStepStepAndExecutedEventByUuid(event.getPayload().getUuid());
        if (executedStep.isFinalized()) {
            executedStepService.publisherExecutedStepFailedResponse(executedStep, executedStep.getStep());
            throw new RuntimeException("This ExecutedStep is already finalized with status: " + executedStep.getStatus());
        }
        Optional<ExecutedStep> newStep = factoryNewStep(
                executedStep.getExecutedEvent().getEventId(),
                executedStep.getStep().getPosition(),
                executedStep.getExecutedEvent(),
                event.getPayload().getPayload()
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
