package ru.tykvin.hermes.user.service;

import io.jsonwebtoken.Jwts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.tykvin.hermes.lib.JsonUtils;
import ru.tykvin.hermes.model.TokenData;

import javax.xml.bind.DatatypeConverter;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:test.properties")
public class TokenServiceTest {

    @Autowired
    private TokenService tokenService;
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
    }

    @Test
    public void createUser() {
    }

    @Test
    public void validateToken() {
    }

    @Test
    public void getToken() {
    }
}
