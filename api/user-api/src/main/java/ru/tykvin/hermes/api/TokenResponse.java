package ru.tykvin.hermes.api;

import lombok.Data;
import lombok.NonNull;
import ru.tykvin.hermes.model.TokenData;

@Data
public class TokenResponse {

    @NonNull
    private final TokenData jwtToken;

}
