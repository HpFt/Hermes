package ru.tykvin.hermes.configuration.token;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tykvin.hermes.lib.JsonUtils;
import ru.tykvin.hermes.model.TokenData;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.Key;

@Component
@RequiredArgsConstructor
public class TokenDataSerializer extends JsonSerializer<TokenData> {

    private final TokenConfiguration cfg;

    @Override
    public void serialize(TokenData tokenData, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(cfg.getApikey());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder()
                .setPayload(JsonUtils.pojoToString(tokenData))
                .signWith(signatureAlgorithm, signingKey);

        gen.writeString(builder.compact());
    }
}
