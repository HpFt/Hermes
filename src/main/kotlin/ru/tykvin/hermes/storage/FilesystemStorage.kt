package ru.tykvin.hermes.storage

import org.apache.commons.io.FileUtils
import org.springframework.stereotype.Service
import ru.tykvin.hermes.configuration.StorageConfiguration
import ru.tykvin.hermes.file.service.dao.FilesystemStorageFile
import ru.tykvin.hermes.file.service.dao.UploadingEntity
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.*

@Service
class FilesystemStorage(
        private val sc: StorageConfiguration
) : Storage {

    private val digest = MessageDigest.getInstance("SHA-256")

    fun read(fileId: UUID): File {
        return resolveFile(fileId.toString()).toFile()
    }

    override fun write(uploadingEntity: UploadingEntity): FilesystemStorageFile {
        val name = uploadingEntity.id.toString()
        val tmpPath = resolveFile("$name.uploading")
        val path = resolveFile(name)
        FileUtils.touch(tmpPath.toFile())
        return uploadingEntity.item.openStream().use { input ->
            Files.copy(input, tmpPath)
            Files.move(tmpPath, path)
            val hash = Base64.getEncoder().encodeToString(digest.digest())
            FilesystemStorageFile(path, Files.size(path), hash)
        }
    }

    private fun delete(path: Path) {
        Files.deleteIfExists(path)
    }

    private fun resolveFile(name: String): Path {
        return Paths.get(sc.root).resolve(Paths.get(name))
    }
}

