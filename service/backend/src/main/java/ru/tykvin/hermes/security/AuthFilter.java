package ru.tykvin.hermes.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.tykvin.hermes.model.TokenData;
import ru.tykvin.hermes.auth.service.TokenService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String xAuth = request.getHeader("X-Authorization");
        try {
            TokenData tokenData = tokenService.getToken(xAuth);
            if (tokenService.validateToken(tokenData, request.getRemoteAddr())) {
                Authentication auth = new TokenAuthentication(new SimpleGrantedAuthority(Roles.USER), tokenData.getUser());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ignored) {
        }
        filterChain.doFilter(request, response);
    }

}
