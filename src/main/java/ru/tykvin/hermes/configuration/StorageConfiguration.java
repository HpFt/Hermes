package ru.tykvin.hermes.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Getter
@Setter
@RequiredArgsConstructor
@Configuration
@ConfigurationProperties("storage")
public class StorageConfiguration {
    private String root;
    private String host;
    private Duration lifeTime;
}
