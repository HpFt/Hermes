package ru.tykvin.hermes.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.tykvin.hermes.auth.service.TokenService;
import ru.tykvin.hermes.model.TokenData;
import ru.tykvin.hermes.model.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private static final String TOKEN_NAME = "token";

    private final TokenService tokenService;
    private final CurrentUserHolder currentUserHolder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        User user;
        String xAuth = request.getHeader(TOKEN_NAME);
        String remoteAddr = request.getRemoteAddr();
        try {
            TokenData tokenData = tokenService.getToken(xAuth);
            user = tokenData.getUser();
            if (!tokenService.validateToken(tokenData, remoteAddr)) {
                user = tokenService.updateUserIp(user, remoteAddr);
                updateToken(user, response);
            }
        } catch (Exception ignored) {
            user = tokenService.signiIn(remoteAddr);
            updateToken(user, response);
        }
        currentUserHolder.set(user);
        filterChain.doFilter(request, response);
    }

    private void updateToken(User user, HttpServletResponse response) {
        response.setHeader(TOKEN_NAME, tokenService.createToken(user));
    }

}
