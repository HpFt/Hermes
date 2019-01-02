package ru.tykvin.hermes.file.service.dao

import org.springframework.data.repository.CrudRepository
import java.util.*

class FilesRepository : CrudRepository<FilesystemStorageFile, UUID> {
}