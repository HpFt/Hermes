package ru.tykvin.hermes.user.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tykvin.hermes.configuration.TokenConfiguration;
import ru.tykvin.hermes.lib.JsonUtils;
import ru.tykvin.hermes.model.TokenData;
import ru.tykvin.hermes.model.User;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenConfiguration cfg;

    public String createToken(String ip) {
        return encrypt(new TokenData(new User(UUID.randomUUID(), LocalDateTime.now(), ip), LocalDateTime.now()));
    }

    public TokenData getToken(String cipher) {
        return decrypt(cipher);
    }

    private String encrypt(TokenData token) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(cfg.getApikey());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        return Jwts.builder()
                .setPayload(JsonUtils.pojoToString(token))
                .signWith(signatureAlgorithm, signingKey)
                .compact();
    }

    private TokenData decrypt(String token) {
        Object json = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(cfg.getApikey()))
                .parse(token).getBody();
        return JsonUtils.getObjectMapper().convertValue(json, TokenData.class);
    }

}
