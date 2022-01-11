package br.com.saga.orchestrator;

import br.com.event.Event;
import br.com.saga.executedEvent.ExecutedEvent;
import br.com.saga.executedEvent.ExecutedEventService;
import br.com.saga.executedStep.ExecutedStep;
import br.com.saga.executedStep.ExecutedStepService;
import br.com.saga.executedStep.OperationStatus;
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
class FallbackListener {
    private final ExecutedStepService executedStepService;
    private final ExecutedEventService executedEventService;

    @RabbitListener(queues = "#{orchestratorFallback.getName()}")
    void fallbackListener(Event<ExecutedStepSuccessMessage, ?> event) {
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
