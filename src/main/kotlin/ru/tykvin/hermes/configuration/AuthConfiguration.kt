package ru.tykvin.hermes.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("api")
class AuthConfiguration {
    lateinit var secret: String
}