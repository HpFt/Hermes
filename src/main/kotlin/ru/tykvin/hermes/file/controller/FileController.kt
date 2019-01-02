package ru.tykvin.hermes.file.controller

import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import ru.tykvin.hermes.auth.service.CurrentUserHolder
import ru.tykvin.hermes.configuration.StorageConfiguration
import ru.tykvin.hermes.file.service.FileService
import java.io.File
import java.io.FileInputStream
import java.nio.file.Paths
import java.util.*
import javax.servlet.http.HttpServletRequest


@RestController("/api/file")
class FileController(
        private val userHolder: CurrentUserHolder,
        private val sc: StorageConfiguration,
        private val fileService: FileService
) {

    @PostMapping("upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun save(request: HttpServletRequest): Set<DownloadingEntity> {
        return fileService.upload(userHolder.get(), request)
    }

    @GetMapping("download/{bindId}")
    fun download(@PathVariable bindId: String): ResponseEntity<Resource> {
        val fileOptional = downloader!!.read(UUID.fromString(bindId))

        if (!fileOptional.isPresent) {
            return ResponseEntity.noContent().build()
        }

        val file = fileOptional.get()

        val resource = InputStreamResource(FileInputStream(asFile(file)))
        val headers = HttpHeaders()
        headers.contentDisposition = ContentDisposition.builder("attachment")
                .filename(file.fileName)
                .size(file.size)
                .build()
        headers.contentLength = file.size
        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(resource)
    }

    private fun asFile(fileInfo: FileInfo): File {
        return Paths.get(sc!!.root).resolve(fileInfo.fileId.toString()).toFile()
    }
}
