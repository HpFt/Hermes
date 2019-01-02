package ru.tykvin.hermes.file.controller

import java.time.Duration
import java.util.*

data class FileInfo(
        val bindingId: UUID,
        val fileId: UUID,
        val fileName: String,
        val lifeTime: Duration,
        val downloadsLeft: Int,
        val size: Long,
        val url: String
)
