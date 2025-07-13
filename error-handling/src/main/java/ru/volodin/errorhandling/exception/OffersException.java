package ru.volodin.errorhandling.exception;

public class OffersException extends RuntimeException {

    private final String rawRemoteError;

    public OffersException(String message, String rawRemoteError, Throwable cause) {
        super(message, cause);
        this.rawRemoteError = rawRemoteError;
    }

    public String getRawRemoteError() {
        return rawRemoteError;
    }
}

