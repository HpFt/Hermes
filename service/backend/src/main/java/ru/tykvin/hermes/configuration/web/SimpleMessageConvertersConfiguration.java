package ru.tykvin.hermes.configuration.web;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.tykvin.hermes.configuration.token.TokenDataDeserializer;
import ru.tykvin.hermes.configuration.token.TokenDataSerializer;
import ru.tykvin.hermes.lib.JsonUtils;
import ru.tykvin.hermes.model.TokenData;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SimpleMessageConvertersConfiguration extends WebMvcConfigurerAdapter {

    private final TokenDataSerializer tokenDataSerializer;
    private final TokenDataDeserializer tokenDataDeserializer;


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new FormHttpMessageConverter());
        ObjectMapper objectMapper = objectMapper();
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
        converters.add(new StringHttpMessageConverter());
    }

    private ObjectMapper objectMapper() {
        return JsonUtils.createObjectMapper()
                .registerModule(createTokenModule());
    }

    private Module createTokenModule() {
        return new SimpleModule()
                .addDeserializer(TokenData.class, tokenDataDeserializer)
                .addSerializer(TokenData.class, tokenDataSerializer);
    }

}