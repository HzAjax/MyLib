package org.example.mylib.config;

import jakarta.servlet.Filter;
import org.example.mylib.filter.LoggingFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonWebAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "app.logging", name = "http-filter-enabled", havingValue = "true", matchIfMissing = false)
    public Filter loggingFilter(LoggingProperties properties) {
        return new LoggingFilter(properties);
    }
}
