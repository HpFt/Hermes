package ru.tykvin.hermes.storage

import java.time.LocalDateTime

class FileBindingConstraints(
        val expiration: LocalDateTime,
        val maxDownloads: Long)
