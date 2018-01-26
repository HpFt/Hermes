package ru.tykvin.hermes.auth.service;

import io.jsonwebtoken.Jwts;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.tykvin.hermes.AbstractTest;
import ru.tykvin.hermes.lib.JsonUtils;
import ru.tykvin.hermes.model.TokenData;
import ru.tykvin.hermes.model.User;

import javax.xml.bind.DatatypeConverter;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static ru.tykvin.hermes.tables.Users.USERS;

public class TokenServiceTest extends AbstractTest {

    @Autowired
    private TokenService tokenService;

    @Value("${api.secret}")
    private String secret;

    @Test
    public void createToken() {
        String cipher = tokenService.createToken(tokenService.signiIn("192.168.0.1"));
        Object token = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secret))
                .parse(cipher).getBody();
        TokenData tokenData = JsonUtils.getObjectMapper().convertValue(token, TokenData.class);
        assertThat(tokenData.getUser().getIp(), equalTo("192.168.0.1"));
        assertThat(checkUser("192.168.0.1"), notNullValue());
    }

    private User checkUser(String ip) {
        return dslContext.selectFrom(USERS).where(USERS.IP.eq(ip)).fetchOptional(r -> new User(UUID.fromString(r.getId()), r.getCreateAt(), r.getIp())).get();
    }
}
