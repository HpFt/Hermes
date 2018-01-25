package ru.tykvin.hermes.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tykvin.hermes.api.TokenResponse;
import ru.tykvin.hermes.auth.service.TokenService;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;

    @GetMapping("/signin")
    private TokenResponse signin(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return new TokenResponse(tokenService.signiIn(ip));
    }

}
