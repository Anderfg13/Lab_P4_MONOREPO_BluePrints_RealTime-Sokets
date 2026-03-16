package co.edu.eci.blueprints.api;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ValidationExceptionHandler {

    private static final String PAYLOAD_VALIDATION_FAILED = "Payload validation failed";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResp<Map<String, String>>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ApiResp<>(400, PAYLOAD_VALIDATION_FAILED, errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResp<Map<String, String>>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ApiResp<>(400, PAYLOAD_VALIDATION_FAILED, errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResp<Map<String, String>>> handleNotReadable(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("body", "Invalid JSON payload");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ApiResp<>(400, PAYLOAD_VALIDATION_FAILED, errors));
    }
}
