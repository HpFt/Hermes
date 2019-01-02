package ru.tykvin.hermes.auth.service

import org.springframework.data.repository.CrudRepository
import ru.tykvin.hermes.auth.controller.User
import java.util.*

interface UserRepository : CrudRepository<User, UUID>