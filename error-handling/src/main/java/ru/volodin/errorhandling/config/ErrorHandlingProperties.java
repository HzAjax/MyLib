package ru.volodin.errorhandling.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("app.errorhandling")
public class ErrorHandlingProperties {
    private boolean enabled = true;
}
