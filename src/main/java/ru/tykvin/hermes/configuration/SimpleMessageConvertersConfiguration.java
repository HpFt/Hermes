package ru.tykvin.hermes.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.tykvin.hermes.lib.JsonUtils;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SimpleMessageConvertersConfiguration implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new FormHttpMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter(JsonUtils.getObjectMapper()));
        converters.add(new StringHttpMessageConverter());
    }

}