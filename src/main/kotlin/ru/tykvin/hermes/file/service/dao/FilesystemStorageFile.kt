package ru.tykvin.hermes.file.service.dao

import java.nio.file.Path

class FilesystemStorageFile(
        val path: Path,
        val size: Long,
        val hash: String)
