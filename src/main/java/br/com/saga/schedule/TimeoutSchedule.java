package br.com.saga.schedule;

import br.com.saga.model.ExecutedEvent;
import br.com.saga.model.ExecutedStep;
import br.com.saga.model.OperationStatus;
import br.com.saga.service.ExecutedEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TimeoutSchedule {
    private final ExecutedEventService executedEventService;

    @Scheduled(fixedDelayString = "${app.scheduled.timeout}")
    void executedEventTimeoutTask() {
        List<ExecutedEvent> executedEvents = executedEventService
                .findAllExecutedEventEventAndExecutedStepsAndExecutedStepsStepByEndIsNull();
        executedEvents.forEach(ee -> {
            Optional<ExecutedStep> executedStepExpired = ee.getExecutedSteps().stream()
                    .filter(ExecutedStep::isNonFinalized)
                    .filter(ExecutedStep::isExpired)
                    .findFirst();
            executedStepExpired.ifPresent(executedStep ->
                    executedEventService.failedExecutedEvent(
                            executedStep.getExecutedEvent(),
                            executedStep,
                            ee.getExecutedSteps().stream()
                                    .filter(it -> !it.equals(executedStep))
                                    .filter(ExecutedStep::isNonFinalized)
                                    .collect(Collectors.toList()),
                            OperationStatus.TIMEOUT
                    )
            );
        });
    }
}
