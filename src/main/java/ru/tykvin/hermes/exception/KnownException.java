package ru.tykvin.hermes.exception;

public class KnownException extends RuntimeException {
    public KnownException(String message) {
        super(message);
    }

    public KnownException() {
    }
}
