package ru.tykvin.hermes.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "User failure")
public class ClientFailureException extends KnownException {
    public ClientFailureException(String message) {
        super(message);
    }
}
