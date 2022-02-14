package br.com.saga.executedEvent;

import br.com.saga.executedStep.OperationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("executed-event")
@RequiredArgsConstructor
class ExecutedEventController {
    private final ExecutedEventService executedEventService;

    @GetMapping
    public ResponseEntity<?> findAllExecutedEventEventAndExecutedStepsByStatus(@RequestParam OperationStatus status) {
        return new ResponseEntity<>(
                executedEventService.findAllExecutedEventEventAndExecutedStepsByStatus(status),
                HttpStatus.OK
        );
    }

    @GetMapping("{uuid}")
    public ResponseEntity<?> findExecutedEventEventAndExecutedStepsByUuid(@PathVariable String uuid) {
        return new ResponseEntity<>(
                executedEventService.findExecutedEventEventAndExecutedStepsByUuid(uuid),
                HttpStatus.OK
        );
    }

    @PostMapping
    public ResponseEntity<ExecutedEventResponse> save(@Valid @RequestBody ExecutedEventRequest executedEventRequest) {
        return new ResponseEntity<>(
                ExecutedEventMapper.INSTANCE.executedEventToExecutedEventResponse(
                        executedEventService.saveAndInitOrchestration(executedEventRequest)
                ),
                HttpStatus.CREATED
        );
    }
}
