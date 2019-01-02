package ru.tykvin.hermes.storage

import ru.tykvin.hermes.file.service.dao.FilesystemStorageFile
import ru.tykvin.hermes.file.service.dao.UploadingEntity

interface Storage {
    fun write(uploadingEntity: UploadingEntity): FilesystemStorageFile
}
