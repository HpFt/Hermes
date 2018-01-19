package ru.tykvin.hermes.user.exception;

public class KnownException extends RuntimeException {
    public KnownException(String message) {
        super(message);
    }

    public KnownException() {
    }
}
