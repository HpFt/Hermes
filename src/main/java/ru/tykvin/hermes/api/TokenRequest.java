package ru.tykvin.hermes.api;

import lombok.Data;
import lombok.NonNull;

@Data
public class TokenRequest {

    @NonNull
    private final String ip;

}
