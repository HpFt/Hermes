package ru.tykvin.hermes.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tykvin.hermes.auth.configuration.AuthConfiguration;
import ru.tykvin.hermes.lib.JsonUtils;
import ru.tykvin.hermes.model.TokenData;
import ru.tykvin.hermes.model.User;
import ru.tykvin.hermes.auth.dao.AuthDao;
import ru.tykvin.hermes.auth.exception.ClientFailureException;
import ru.tykvin.hermes.auth.exception.NeedUpdateTokenException;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final AuthConfiguration cfg;
    private final AuthDao authDao;

    @Transactional
    public String signiIn(String ip) {
        User user = authDao.findUserByIp(ip).orElseGet(() -> createUser(ip));
        return encrypt(new TokenData(user, LocalDateTime.now()));
    }

    public User createUser(String ip) {
        return authDao.createUser(new User(UUID.randomUUID(), LocalDateTime.now(), ip));
    }

    public boolean validateToken(TokenData tokenData, String currentIp) throws NeedUpdateTokenException {
        User user = tokenData.getUser();
        return user.getIp().equals(currentIp);
    }

    public TokenData getToken(String cipher) {
        return decrypt(cipher);
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
