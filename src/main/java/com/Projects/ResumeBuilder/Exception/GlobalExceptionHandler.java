package com.Projects.ResumeBuilder.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidationException(
            MethodArgumentNotValidException exception){
        log.info("Inside GLobalExceptionHandler - handleValidationException()");
        Map<String,String> errors= new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName,errorMessage);
        });

        Map<String,Object> response = new HashMap<>();
        response.put("message","Validation failed");
        response.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ResumeBuilderException.class)
    public ResponseEntity<Map<String,Object>> handleResourceExistsException(ResumeBuilderException exception){
        log.info("Inside GLobalExceptionHandler - handleResourceExistsException()");
        Map<String,Object> response = new HashMap<>();
        response.put("message","Resource exists");
        response.put("errors", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGenericException(Exception exception){
        log.info("Inside GLobalExceptionHandler - handleGenericException()");
        Map<String,Object> response = new HashMap<>();
        response.put("message","Something went wrong. Contact administrator");
        response.put("errors", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
