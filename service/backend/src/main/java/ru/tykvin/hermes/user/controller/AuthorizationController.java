package ru.tykvin.hermes.user.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tykvin.hermes.api.TokenResponse;
import ru.tykvin.hermes.user.exception.KnownException;
import ru.tykvin.hermes.user.exception.NeedUpdateTokenException;
import ru.tykvin.hermes.user.service.TokenService;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthorizationController {

    private final TokenService tokenService;

    @GetMapping("/signin")
    private TokenResponse getToken(@RequestHeader("IP-address") String ip) {
        return new TokenResponse(tokenService.createToken(ip));
    }

    @GetMapping("/test")
    private void doSomething(
            @RequestHeader("IP-address") String ip,
            @RequestHeader(value = "token", required = false) String token
    ) {
        try {
            tokenService.validateToken(token, ip);
        } catch (NeedUpdateTokenException e) {
            tokenService.createUser(ip);
            throw e;
        }
    }

}
