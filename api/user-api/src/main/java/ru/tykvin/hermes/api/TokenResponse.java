package ru.tykvin.hermes.api;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;


@Getter
@ToString
@RequiredArgsConstructor
public class TokenResponse {

    @NonNull
    private final String token;

}
