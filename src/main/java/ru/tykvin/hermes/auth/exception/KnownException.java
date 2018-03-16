package ru.tykvin.hermes.auth.exception;

public class KnownException extends RuntimeException {
    public KnownException(String message) {
        super(message);
    }

    public KnownException() {
    }
}
