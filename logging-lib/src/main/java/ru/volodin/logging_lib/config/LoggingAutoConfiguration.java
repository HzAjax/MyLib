package ru.volodin.logging_lib.config;

import jakarta.servlet.Filter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.volodin.logging_lib.filter.LoggingFilter;

@AutoConfiguration
public class LoggingAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "app.logging")
    public LoggingProperties loggingProperties() {
        return new LoggingProperties();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "app.logging", name = "http-filter-enabled",
            havingValue = "true", matchIfMissing = false)
    public Filter loggingFilter(LoggingProperties properties) {
        return new LoggingFilter(properties);
    }
}
