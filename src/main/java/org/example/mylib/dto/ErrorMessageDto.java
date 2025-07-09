package org.example.mylib.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(
        description = "DTO that describes error details returned in case of API failure or validation error"
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessageDto {
    private String message;
    private int status;
    private String path;
    private LocalDateTime timestamp;
}
