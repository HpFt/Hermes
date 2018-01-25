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

import static credit.station.jooq.tables.Users.USERS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TokenServiceTest extends AbstractTest {

    @Autowired
    private TokenService tokenService;

    @Value("${api.secret}")
    private String secret;

    @Test
    public void createToken() {
        String cipher = tokenService.signiIn("192.168.0.1");
        Object token = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secret))
                .parse(cipher).getBody();
        TokenData tokenData = JsonUtils.getObjectMapper().convertValue(token, TokenData.class);
        assertThat(tokenData.getUser().getIp(), equalTo("192.168.0.1"));
        assertThat(checkUser("192.168.0.1"), notNullValue());
    }

    @Test
    public void createUser() {
        User user = tokenService.createUser("192.168.0.2");
        assertThat(checkUser("192.168.0.2"), equalTo(user));
    }

    @Test
    public void getToken() {
        String cipher = tokenService.signiIn("192.168.0.5");
        TokenData tokenData = tokenService.getToken(cipher);
        assertThat(tokenData.getUser().getIp(), equalTo("192.168.0.5"));
    }

    private User checkUser(String ip) {
        return dslContext.selectFrom(USERS).where(USERS.IP.eq(ip)).fetchOptional(r -> new User(UUID.fromString(r.getId()), r.getCreateAt(), r.getIp())).get();
    }
}
