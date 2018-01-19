package ru.tykvin.hermes.user.service;

import org.springframework.stereotype.Service;
import ru.tykvin.hermes.model.TokenData;
import ru.tykvin.hermes.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenService {

    public TokenData createToken(String ip) {
        return new TokenData(new User(UUID.randomUUID(), LocalDateTime.now(), ip), LocalDateTime.now());
    }
}
