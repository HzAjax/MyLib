package ru.volodin.errorhandling_lib.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import ru.volodin.errorhandling_lib.exception.ControllerExceptionHandler;

@AutoConfiguration
public class ErrorHandlingAutoConfiguration {

    @Bean
    public ControllerExceptionHandler controllerExceptionHandler(ObjectMapper objectMapper) {
        return new ControllerExceptionHandler(objectMapper);
    }
}
