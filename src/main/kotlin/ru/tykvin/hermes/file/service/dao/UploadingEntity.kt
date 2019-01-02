package ru.tykvin.hermes.file.service.dao

import org.apache.tomcat.util.http.fileupload.FileItemStream
import java.time.OffsetDateTime
import java.util.*

class UploadingEntity(
        val item: FileItemStream,
        val id: UUID = UUID.randomUUID(),
        val creatAt: OffsetDateTime = OffsetDateTime.now(),
        val size: Long = 0,
        val fileName: String,
        val sha256: String,
        val uploaded: Boolean = false,
        val tmp: Boolean = true
)
