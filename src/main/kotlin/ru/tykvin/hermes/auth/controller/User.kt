package ru.tykvin.hermes.auth.controller

import org.springframework.data.annotation.CreatedDate
import java.time.OffsetDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class User(
        @Id
        @GeneratedValue
        val id: UUID,
        @CreatedDate
        val createAt: OffsetDateTime,
        val ip: String)
