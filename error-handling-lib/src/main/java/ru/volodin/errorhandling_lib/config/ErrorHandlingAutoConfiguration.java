package ru.volodin.errorhandling_lib.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.volodin.errorhandling_lib.exception.BaseExceptionHandler;

@AutoConfiguration
@EnableConfigurationProperties(ErrorHandlingProperties.class)
public class ErrorHandlingAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "app.errorhandling", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public BaseExceptionHandler controllerExceptionHandler(ObjectMapper objectMapper) {
        return new BaseExceptionHandler(objectMapper);
    }
}
