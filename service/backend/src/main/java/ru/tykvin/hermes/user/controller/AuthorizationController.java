package ru.tykvin.hermes.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tykvin.hermes.api.TokenRequest;
import ru.tykvin.hermes.api.TokenResponse;
import ru.tykvin.hermes.user.service.TokenService;

@RestController
@RequiredArgsConstructor
public class AuthorizationController {

    private final TokenService tokenService;

    @GetMapping
    private TokenResponse getToken() {
        return new TokenResponse(tokenService.createToken("localhost"));
    }

    @PostMapping("/decode")
    private String decodeToken(TokenResponse tokenResponse) {
        return tokenResponse.toString();
    }

}
