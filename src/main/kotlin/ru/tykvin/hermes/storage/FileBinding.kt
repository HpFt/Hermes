package ru.tykvin.hermes.storage

import ru.tykvin.hermes.auth.controller.User
import ru.tykvin.hermes.file.controller.DownloadingEntity
import java.time.LocalDateTime

class FileBinding(
        val downloadingEntity: DownloadingEntity,
        val user: User,
        val constraints: FileBindingConstraints,
        val createAt: LocalDateTime)
