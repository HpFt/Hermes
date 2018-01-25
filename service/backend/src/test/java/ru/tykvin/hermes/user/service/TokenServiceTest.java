package ru.tykvin.hermes.user.service;

import io.jsonwebtoken.Jwts;
import org.jooq.DSLContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.tykvin.hermes.PostgresqlEmbeddedConfiguration;
import ru.tykvin.hermes.lib.JsonUtils;
import ru.tykvin.hermes.model.TokenData;
import ru.tykvin.hermes.model.User;
import ru.tykvin.hermes.user.exception.NeedUpdateTokenException;

import javax.xml.bind.DatatypeConverter;
import java.util.UUID;

import static credit.station.jooq.tables.Users.USERS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@Import(PostgresqlEmbeddedConfiguration.class)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:test.properties")
public class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private DSLContext dslContext;

    @Value("${api.secret}")
    private String secret;

    @Test
    public void createToken() {
        String cipher = tokenService.createToken("192.168.0.1");
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
    public void validToken() {
        String cipher = tokenService.createToken("192.168.0.3");
        tokenService.validateToken(cipher, "192.168.0.3");
    }

    @Test(expected = NeedUpdateTokenException.class)
    public void notValidToken() {
        String cipher = tokenService.createToken("192.168.0.4");
        tokenService.validateToken(cipher, "192.168.0.2");
    }

    @Test
    public void getToken() {
        String cipher = tokenService.createToken("192.168.0.5");
        TokenData tokenData = tokenService.getToken(cipher);
        assertThat(tokenData.getUser().getIp(), equalTo("192.168.0.5"));
    }

    private User checkUser(String ip) {
        return dslContext.selectFrom(USERS).where(USERS.IP.eq(ip)).fetchOptional(r -> new User(UUID.fromString(r.getId()), r.getCreateAt(), r.getIp())).get();
    }
}
