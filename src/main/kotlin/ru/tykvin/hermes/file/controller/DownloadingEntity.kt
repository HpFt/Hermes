package ru.tykvin.hermes.file.controller

import java.time.OffsetDateTime
import java.util.*

class DownloadingEntity(
        val bindId: UUID,
        val size: Long,
        val createAt: OffsetDateTime,
        val fileName: String)
