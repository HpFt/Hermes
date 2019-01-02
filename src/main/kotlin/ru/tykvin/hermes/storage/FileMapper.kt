package ru.tykvin.hermes.storage

import org.apache.tomcat.util.http.fileupload.FileItemStream
import ru.tykvin.hermes.configuration.StorageConfiguration
import ru.tykvin.hermes.file.service.dao.UploadingEntity

class FilesMapper(
        private val sc: StorageConfiguration) {

    fun mapToUploadingEntity(item: FileItemStream): UploadingEntity {
        val headers = item.headers
        return UploadingEntity(
                item = item,
                fileName = item.name,
                sha256 = headers.getHeader("sha256")
        )
    }
}