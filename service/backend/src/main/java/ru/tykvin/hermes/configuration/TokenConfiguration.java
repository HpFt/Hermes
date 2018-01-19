package ru.tykvin.hermes.configuration;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@RequiredArgsConstructor
@Configuration
@ConfigurationProperties("security")
public class TokenConfiguration {

    private String apikey;

}
