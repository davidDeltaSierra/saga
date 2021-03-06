package br.com.saga.executedStep;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("executed-step")
@RequiredArgsConstructor
class ExecutedStepController {
    private final ExecutedStepService executedStepService;

    @GetMapping("{uuid}")
    public ResponseEntity<?> findByUuid(@PathVariable String uuid) {
        return new ResponseEntity<>(
                executedStepService.findByUuid(uuid),
                HttpStatus.OK
        );
    }
}
