package ru.tykvin.hermes.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tykvin.hermes.auth.configuration.AuthConfiguration;
import ru.tykvin.hermes.auth.dao.AuthDao;
import ru.tykvin.hermes.auth.exception.ClientFailureException;
import ru.tykvin.hermes.auth.exception.NeedUpdateTokenException;
import ru.tykvin.hermes.lib.JsonUtils;
import ru.tykvin.hermes.model.TokenData;
import ru.tykvin.hermes.model.User;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final AuthConfiguration cfg;
    private final AuthDao authDao;

    @Transactional
    public User signiIn(String ip) {
        return authDao.findUserByIp(ip).orElseGet(() -> createUser(ip));
    }

    private User createUser(String ip) {
        return authDao.createUser(new User(UUID.randomUUID(), OffsetDateTime.now(), ip));
    }

    public boolean validateToken(TokenData tokenData, String currentIp) throws NeedUpdateTokenException {
        User user = tokenData.getUser();
        return user.getIp().equals(currentIp);
    }

    public TokenData getToken(String cipher) {
        return decrypt(cipher);
    }

    public User updateUserIp(User user, String remoteAddr) {
        return authDao.updateUserIp(user.getId(), remoteAddr);
    }

    public String createToken(User user) {
        return encrypt(new TokenData(user));
    }

    private String encrypt(TokenData token) {
        try {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(cfg.getSecret());
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
            return Jwts.builder()
                    .setPayload(JsonUtils.pojoToString(token))
                    .signWith(signatureAlgorithm, signingKey)
                    .compact();
        } catch (Exception e) {
            throw new ClientFailureException(e.getMessage());
        }
    }

    private TokenData decrypt(String token) {
        try {
            Object json = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(cfg.getSecret()))
                    .parse(token).getBody();
            return JsonUtils.getObjectMapper().convertValue(json, TokenData.class);
        } catch (Exception e) {
            throw new ClientFailureException(e.getMessage());
        }
    }
}
