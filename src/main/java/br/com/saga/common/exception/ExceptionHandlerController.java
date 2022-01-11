package br.com.saga.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> responseStatusException(ResponseStatusException ex) {
        String message = ex.getReason();
        log.error("ResponseStatusException: {}", message);
        return factoryResponseEntityWithResponseEntityException(message, ex.getStatus(), ex);
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<?> httpStatusCodeException(HttpStatusCodeException ex) {
        String message = ex.getResponseBodyAsString();
        log.error("HttpStatusCodeException: {}", message);
        return factoryResponseEntityWithResponseEntityException(message, ex.getStatusCode(), ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = getStringErrors(ex.getBindingResult());
        log.error("MethodArgumentNotValidException: {}", message);
        return factoryResponseEntityWithResponseEntityException(message, HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String message = ex.getMessage();
        log.error("HttpRequestMethodNotSupportedException: {}", message);
        return factoryResponseEntityWithResponseEntityException(message, HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = getAllMessageFromException(ex);
        log.error("HttpMessageNotReadableException: {}", message);
        return factoryResponseEntityWithResponseEntityException(message, HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> dataIntegrityViolationException(DataIntegrityViolationException ex) {
        String message = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "dataIntegrityViolationException";
        log.error("DataIntegrityViolationException: {}", message);
        return factoryResponseEntityWithResponseEntityException(message, HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<?> missingRequestHeaderException(MissingRequestHeaderException ex) {
        String message = "Header '" + ex.getHeaderName() + "' is required";
        log.error("MissingRequestHeaderException: {}", message);
        return factoryResponseEntityWithResponseEntityException(message, HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> missingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String message = "Parameter '" + ex.getParameterName() + "' type '" + ex.getParameterType() + "' is required";
        log.error("MissingRequestHeaderException: {}", message);
        return factoryResponseEntityWithResponseEntityException(message, HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> bindException(BindException ex) {
        String message = "Query params errors, " + getStringErrors(ex.getBindingResult());
        log.error("BindException: {}", message);
        return factoryResponseEntityWithResponseEntityException(message, HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = String.format(
                "Failed to convert parameter %s, value %s, to %s",
                ex.getName(),
                ex.getValue(),
                nonNull(ex.getRequiredType()) ? ex.getRequiredType().getSimpleName() : "Unknown"
        );
        log.error("MethodArgumentTypeMismatchException: {}", message);
        return factoryResponseEntityWithResponseEntityException(message, HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception ex) {
        log.error("Exception", ex);
        return factoryResponseEntityWithResponseEntityException(getAllMessageFromException(ex), HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    private String getStringErrors(BindingResult bindingResult) {
        return bindingResult
                .getFieldErrors().stream()
                .map(it -> it.getField() + ": " + it.getDefaultMessage())
                .collect(Collectors.joining(", "));
    }

    private ResponseEntity<?> factoryResponseEntityWithResponseEntityException(String message, HttpStatus httpStatus, Exception exception) {
        return new ResponseEntity<>(
                ResponseEntityException.builder()
                        .timestamp(System.currentTimeMillis())
                        .message(message)
                        .status(httpStatus)
                        .build(),
                httpStatus
        );
    }

    private String getAllMessageFromException(Throwable throwable) {
        List<String> messages = new ArrayList<>(5);
        Throwable rootCause = throwable;
        do {
            messages.add(rootCause.getClass().getSimpleName() + ": " + rootCause.getMessage());
            rootCause = rootCause.getCause();
        } while (nonNull(rootCause));
        return String.join(" && ", messages);
    }
}
