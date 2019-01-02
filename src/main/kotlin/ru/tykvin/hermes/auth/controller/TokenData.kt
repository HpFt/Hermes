package ru.tykvin.hermes.auth.controller

import java.time.LocalDateTime

class TokenData(
        val user: User,
        val createAt: LocalDateTime = LocalDateTime.now()
)
