package ru.tykvin.hermes.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Need update token")
public class NeedUpdateTokenException extends KnownException {

}
