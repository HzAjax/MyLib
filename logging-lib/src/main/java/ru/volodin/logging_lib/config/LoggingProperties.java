package ru.volodin.logging_lib.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.logging")
public class LoggingProperties {
    private boolean httpFilterEnabled;
    private String level;
}