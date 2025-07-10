package org.example.mylib.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mylib.dto.ErrorMessageDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {

    private final ObjectMapper objectMapper;

    private ErrorMessageDto buildError(String message, HttpStatus status, WebRequest request) {
        return ErrorMessageDto.builder()
                .message(message)
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
    }

    private ErrorMessageDto buildError(String message, HttpStatus status, WebRequest request, ErrorMessageDto details) {
        return ErrorMessageDto.builder()
                .message(message)
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .details(details)
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageDto> handlerStatementNotFoundException(EntityNotFoundException e, WebRequest request) {
        log.error("Error = {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildError(e.getMessage(), HttpStatus.NOT_FOUND, request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageDto> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.error("Error = {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError(message, HttpStatus.BAD_REQUEST, request));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessageDto> handleHttpMessageNotReadable(HttpMessageNotReadableException e, WebRequest request) {
        String message = e.getMostSpecificCause().getMessage();
        log.warn("Malformed JSON or unreadable body: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError("Invalid request body: " + message, HttpStatus.BAD_REQUEST, request));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessageDto> handleMethodNotSupported(HttpRequestMethodNotSupportedException e, WebRequest request) {
        String message = "HTTP method not supported: " + e.getMethod();
        log.warn(message);
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(buildError(message, HttpStatus.METHOD_NOT_ALLOWED, request));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorMessageDto> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e, WebRequest request) {
        String message = "Unsupported media type: " + e.getContentType();
        log.warn(message);
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(buildError(message, HttpStatus.UNSUPPORTED_MEDIA_TYPE, request));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorMessageDto> handleMissingParameter(MissingServletRequestParameterException e, WebRequest request) {
        String message = "Missing request parameter: " + e.getParameterName();
        log.warn(message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError(message, HttpStatus.BAD_REQUEST, request));
    }

    @ExceptionHandler(ScoringException.class)
    public ResponseEntity<ErrorMessageDto> handleScoringException(ScoringException e, WebRequest request) {
        ErrorMessageDto nested = null;

        String raw = e.getRawRemoteError();
        try {
            if (raw != null) {
                if (raw.startsWith("\"{") && raw.endsWith("}\"")) {
                    String unquoted = objectMapper.readValue(raw, String.class);
                    nested = objectMapper.readValue(unquoted, ErrorMessageDto.class);
                } else if (raw.startsWith("{")) {
                    nested = objectMapper.readValue(raw, ErrorMessageDto.class);
                }
            }
        } catch (Exception ex) {
            log.warn("Failed to deserialize nested ErrorMessageDto from ScoringException", ex);
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(
                        e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        request,
                        nested
                ));
    }

    @ExceptionHandler(OffersException.class)
    public ResponseEntity<ErrorMessageDto> handleOffersException(OffersException e, WebRequest request) {
        ErrorMessageDto nested = null;
        try {
            String raw = e.getRawRemoteError();
            if (raw != null) {
                if (raw.startsWith("\"{") && raw.endsWith("}\"")) {
                    String unquoted = objectMapper.readValue(raw, String.class);
                    nested = objectMapper.readValue(unquoted, ErrorMessageDto.class);
                } else if (raw.startsWith("{")) {
                    nested = objectMapper.readValue(raw, ErrorMessageDto.class);
                }
            }
        } catch (Exception ex) {
            log.warn("Failed to deserialize nested ErrorMessageDto from OffersException", ex);
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(
                        e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        request,
                        nested
                ));
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ErrorMessageDto> handleHttpClientException(HttpStatusCodeException e, WebRequest request) {
        log.error("Remote service error response: {}", e.getResponseBodyAsString());

        ErrorMessageDto nested = null;
        try {
            nested = objectMapper.readValue(e.getResponseBodyAsString(), ErrorMessageDto.class);
        } catch (Exception ex) {
            log.warn("Failed to deserialize nested ErrorMessageDto", ex);
        }

        String topMessage = "Remote service error: " + (nested != null ? nested.getMessage() : e.getStatusText());

        return ResponseEntity
                .status(e.getStatusCode())
                .body(buildError(topMessage, (HttpStatus) e.getStatusCode(), request, nested));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDto> handleAllUncaughtException(Exception e, WebRequest request) {
        log.error("Unexpected error occurred: ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request));
    }
}
