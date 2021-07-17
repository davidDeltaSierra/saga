package br.com.saga.controller;

import br.com.saga.dto.request.ExecutedEventRequest;
import br.com.saga.dto.response.ExecutedEventResponse;
import br.com.saga.mapper.ExecutedEventMapper;
import br.com.saga.model.OperationStatus;
import br.com.saga.service.ExecutedEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("executed-event")
@RequiredArgsConstructor
public class ExecutedEventController {
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
