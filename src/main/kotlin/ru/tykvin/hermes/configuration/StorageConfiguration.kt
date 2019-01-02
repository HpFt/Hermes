package ru.tykvin.hermes.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

import java.time.Duration

@Component
@ConfigurationProperties("storage")
class StorageConfiguration {
    lateinit var root: String
    lateinit var host: String
    lateinit var lifeTime: Duration
}
