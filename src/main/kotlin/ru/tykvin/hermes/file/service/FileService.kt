package ru.tykvin.hermes.file.service

import org.apache.tomcat.util.http.fileupload.FileItemStream
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload
import org.springframework.stereotype.Service
import ru.tykvin.hermes.auth.controller.User
import ru.tykvin.hermes.configuration.StorageConfiguration
import ru.tykvin.hermes.file.controller.DownloadingEntity
import ru.tykvin.hermes.storage.FilesystemStorage
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.servlet.http.HttpServletRequest

@Service
class FileService(
        val storage: FilesystemStorage
) {

    fun upload(user: User, request: HttpServletRequest): Set<DownloadingEntity> {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw IllegalArgumentException("It's not multipart")
        }
        val fileUpload = ServletFileUpload()
        val iterator = fileUpload.getItemIterator(request)
        val result = HashSet<DownloadingEntity>()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.isFormField) {
                continue
            }
            storage.write(item)
        }
        return result
    }

}