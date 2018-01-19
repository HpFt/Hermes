package ru.tykvin.hermes.user.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tykvin.hermes.configuration.TokenConfiguration;
import ru.tykvin.hermes.lib.JsonUtils;
import ru.tykvin.hermes.model.TokenData;
import ru.tykvin.hermes.model.User;
import ru.tykvin.hermes.user.dao.UserDao;
import ru.tykvin.hermes.user.exception.ClientFailureException;
import ru.tykvin.hermes.user.exception.NeedUpdateTokenException;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenConfiguration cfg;
    private final UserDao userDao;

    @Transactional
    public String createToken(String ip) {
        User user = userDao.findUserByIp(ip).orElse(createUser(ip));
        return encrypt(new TokenData(user, LocalDateTime.now()));
    }

    public User createUser(String ip) {
        return userDao.createUser(new User(UUID.randomUUID(), LocalDateTime.now(), ip));
    }

    public void validateToken(String cipherToken, String currentIp) throws NeedUpdateTokenException {
        TokenData tokenData = decrypt(cipherToken);
        User user = tokenData.getUser();
        if (!user.getIp().equals(currentIp)) {
            throw new NeedUpdateTokenException();
        }
    }

    public TokenData getToken(String cipher) {
        return decrypt(cipher);
    }

    private String encrypt(TokenData token) {
        try {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(cfg.getApikey());
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
                    .setSigningKey(DatatypeConverter.parseBase64Binary(cfg.getApikey()))
                    .parse(token).getBody();
            return JsonUtils.getObjectMapper().convertValue(json, TokenData.class);
        } catch (Exception e) {
            throw new ClientFailureException(e.getMessage());
        }
    }

}
