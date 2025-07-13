package ru.volodin.errorhandling.exception;

public class ScoringException extends RuntimeException {

    private final String rawRemoteError;

    public ScoringException(String message) {
        super(message);
        this.rawRemoteError = null;
    }

    public ScoringException(String message, String rawRemoteError, Throwable cause) {
        super(message, cause);
        this.rawRemoteError = rawRemoteError;
    }

    public String getRawRemoteError() {
        return rawRemoteError;
    }
}

