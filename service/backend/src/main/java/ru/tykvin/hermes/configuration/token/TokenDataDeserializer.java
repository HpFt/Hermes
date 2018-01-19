package ru.tykvin.hermes.configuration.token;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tykvin.hermes.lib.JsonUtils;
import ru.tykvin.hermes.model.TokenData;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenDataDeserializer extends JsonDeserializer<TokenData> {

    private final TokenConfiguration cfg;
    private final ObjectMapper om = JsonUtils.createObjectMapper();

    @Override
    public TokenData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String json = (String) Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(cfg.getApikey()))
                .parse(p.getValueAsString()).getBody();
        return om.readValue(json, TokenData.class);
    }
}
