package br.com.saga.event;

import br.com.event.Event;
import br.com.saga.dto.message.ExecutedStepSuccessMessage;
import br.com.saga.model.ExecutedEvent;
import br.com.saga.model.ExecutedStep;
import br.com.saga.model.OperationStatus;
import br.com.saga.service.ExecutedEventService;
import br.com.saga.service.ExecutedStepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FallbackListener {
    private final ExecutedStepService executedStepService;
    private final ExecutedEventService executedEventService;

    @RabbitListener(queues = "#{orchestratorFallback.getName()}")
    void fallbackListener(Event<ExecutedStepSuccessMessage, OrchestratorEvent> event) {
        ExecutedStep executedStep = executedStepService.findByUuid(event.getPayload().getUuid());
        if (executedStep.isFinalized()) {
            throw new RuntimeException("This ExecutedStep is already finalized with status: " + executedStep.getStatus());
        }
        ExecutedEvent executedEvent = executedEventService
                .findExecutedEventEventAndExecutedStepsById(executedStep.getExecutedEventId());
        executedEventService.failedExecutedEvent(
                executedEvent,
                executedStep,
                getExecutedStepsFinalizedNotIncludes(executedEvent.getExecutedSteps(), executedStep),
                OperationStatus.FAILED
        );
    }

    private List<ExecutedStep> getExecutedStepsFinalizedNotIncludes(Set<ExecutedStep> executedSteps, ExecutedStep executedStep) {
        return executedSteps.stream()
                .filter(it -> !it.equals(executedStep))
                .filter(ExecutedStep::isFinalized)
                .collect(Collectors.toList());
    }
}
