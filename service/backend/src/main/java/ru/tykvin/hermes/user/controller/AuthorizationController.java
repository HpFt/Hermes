package ru.tykvin.hermes.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tykvin.hermes.api.TokenResponse;
import ru.tykvin.hermes.user.service.TokenService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthorizationController {

    private final TokenService tokenService;

    @GetMapping("/signin")
    private TokenResponse getToken(@RequestHeader("IP-address") String ip) {
        return new TokenResponse(tokenService.createToken(ip));
    }

}
