package ru.tykvin.hermes.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.tykvin.hermes.model.User;

import java.util.Collections;

public class TokenAuthentication extends AbstractAuthenticationToken {
    private User authenticatedUser;

    TokenAuthentication(SimpleGrantedAuthority role, User authenticatedUser) {
        super(Collections.singletonList(role));
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public Object getCredentials() {
        return authenticatedUser.getId();
    }

    @Override
    public Object getPrincipal() {
        return authenticatedUser;
    }

}
