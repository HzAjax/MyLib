package ru.volodin.errorhandling_lib.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler extends BaseExceptionHandler {

    public ControllerExceptionHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }
}
