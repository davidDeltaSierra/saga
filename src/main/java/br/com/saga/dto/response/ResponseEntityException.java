package br.com.saga.dto.response;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.HttpStatus;

@Value
@Builder
@Jacksonized
public class ResponseEntityException {
    Long timestamp;
    String message;
    HttpStatus status;
}
